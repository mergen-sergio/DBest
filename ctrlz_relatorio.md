# Revisão da Implementação: Ctrl+Z / Ctrl+Y

## O que foi implementado

Implementei o sistema de desfazer/refazer (Ctrl+Z e Ctrl+Y) no canvas. A abordagem escolhida foi baseada em **snapshots do estado completo**, e não no Command Pattern que eu tentei primeiro.

---

## Por que abandonei o Command Pattern

A primeira tentativa usou Command Pattern — a ideia era que cada operação tivesse um método `undo()` que revertesse exatamente o que foi feito. Não funcionou bem por alguns motivos:

- Comandos aninhados acabavam criando sub-stacks isolados, o que quebrava a ordem das operações
- O `CommandController` limpava o stack em momentos que não eram os certos
- Reverter operações do mxGraph de forma inversa é muito frágil na prática

Decidi trocar por algo mais simples e confiável.

---

## A solução: dois stacks de snapshots

Em vez de tentar reverter operações, agora o sistema tira uma "foto" do estado completo do canvas antes de qualquer ação. São dois stacks (deques), um para desfazer e um para refazer:

```
UNDO STACK          REDO STACK
[ snap4 ] ← topo   [ snap5 ] ← topo
[ snap3 ]
[ snap2 ]
[ snap1 ]           (máximo 10 cada)
```

**Como funciona o Ctrl+Z:**

1. Salva o estado atual no redo stack
2. Pega o snapshot do topo do undo stack
3. Restaura aquele snapshot no canvas

**Como funciona o Ctrl+Y:** o inverso — pega do redo stack e salva no undo stack.

**Nova ação após um undo:** limpa o redo stack inteiro. Comportamento padrão de qualquer editor.

---

## O que cada snapshot guarda

A classe `CanvasSnapshot.java` captura, para cada célula ativa no canvas:

- Posição (x, y, largura, altura)
- Tipo da célula (`CSV`, `OPERATION`, `MEMORY`, etc.)
- Nome, alias e argumentos
- Se está inicializada (`true`/`false`)
- Todas as conexões (source → target)

Na restauração, o graph e o `CellRepository` são limpos completamente e reconstruídos do zero a partir dos dados salvos.

---

## Como o snapshot é disparado automaticamente

Não precisei modificar cada comando individualmente. O `CommandController` tem um campo `static Runnable beforeExecuteHook`, e o `MainController` registra `undoRedoManager::saveSnapshot` nele. Assim, todo comando já salva o snapshot automaticamente antes de executar:

```
CommandController.execute(cmd)
    └─ beforeExecuteHook.run()    ← chama UndoRedoManager.saveSnapshot()
    └─ cmd.execute()              ← ação real
```

---

## Resumo das decisões

| Aspecto       | Decisão                                |
| ------------- | -------------------------------------- |
| Estrutura     | 2× `ArrayDeque<CanvasSnapshot>`        |
| Limite        | 10 snapshots por direção               |
| Granularidade | Estado completo do canvas              |
| Trigger       | Hook automático no `CommandController` |
| Complexidade  | Baixa — ~150 linhas no total           |

---

## Avaliação geral

A solução ficou simples e direta. O maior benefício é que ela restaura o estado **exato** do canvas — não tenta recalcular o inverso de uma operação, o que eliminaria uma categoria inteira de bugs difíceis de reproduzir. O limite de 10 snapshots por direção é razoável para o uso esperado e evita consumo excessivo de memória.
