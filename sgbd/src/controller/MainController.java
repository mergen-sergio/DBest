package controller;

import java.awt.Container;
import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource;

import dsl.entities.BinaryExpression;
import dsl.entities.OperationExpression;
import dsl.entities.Relation;
import entities.Action.CreateOperationAction;
import entities.Action.CreateTableAction;
import entities.Action.CurrentAction;
import entities.Action.CurrentAction.ActionType;
import entities.Edge;
import entities.Tree;
import entities.buttons.Button;
import entities.buttons.OperationButton;
import entities.cells.Cell;
import entities.cells.OperationCell;
import entities.cells.TableCell;
import entities.utils.CellUtils;
import entities.utils.TreeUtils;
import enums.OperationType;
import files.ImportFile;
import gui.frames.CellInformationFrame;
import gui.frames.dsl.Console;
import gui.frames.dsl.TextEditor;
import gui.frames.forms.create.FormFrameCreateTable;
import gui.frames.forms.importexport.FormFrameExportTable;
import gui.frames.forms.importexport.FormFrameImportAs;
import gui.frames.main.MainFrame;
import sgbd.table.Table;
import util.Export;

public class MainController extends MainFrame {

	private static final Map<Integer, Tree> trees = new HashMap<>();
	private static File lastDirectory = new File("");

	private final Container textEditor = new TextEditor(this).getContentPane();

	private mxCell jCell;
	private mxCell ghostJCell = null;

	private final AtomicReference<CurrentAction> currentActionRef = new AtomicReference<>();
	private final AtomicReference<Edge> edgeRef = new AtomicReference<>(new Edge());

	private static int yTables = 0;
	private static final Map<String, Table> tables = new HashMap<>();
	public boolean clicked = false;

	private static final Set<Button<?>> buttons = new HashSet<>();

	public MainController() {

		super(buttons);

		tablesComponent.getGraphControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if ((mxCell) tablesComponent.getCellAt(e.getX(), e.getY()) != null) {
					clicked = true;
					graph.setSelectionCell(
							new mxCell(((mxCell) tablesComponent.getCellAt(e.getX(), e.getY())).getValue()));
				}
			}
		});

		graph.addListener(mxEvent.CELLS_ADDED, new mxEventSource.mxIEventListener() {
			@Override
			public void invoke(Object sender, mxEventObject evt) {
				if (clicked) {
					CellUtils.verifyCell((mxCell) graph.getSelectionCell(), ghostJCell);
					clicked = false;
				}
			}
		});

		addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {

				File directory = new File(".");
				File[] filesList = directory.listFiles();
				assert filesList != null;
				for (File file : filesList) 
					if (file.isFile() && (file.getName().endsWith(".dat") || file.getName().endsWith(".head")))
						file.delete();

				System.exit(0);

			}

		});

	}

	@SuppressWarnings("incomplete-switch")
	@Override
	public void actionPerformed(ActionEvent e) {

		graph.removeCells(new Object[] { ghostJCell }, true);
		ghostJCell = null;

		Button<?> btnClicked = buttons.stream().filter(x -> x.getButton() == e.getSource()).findAny().orElse(null);
		String style = null;

		if (btnClicked != null) {

			edgeRef.get().reset();
			btnClicked.setCurrentAction(currentActionRef);

			switch (currentActionRef.get().getType()) {

			case DELETE_CELL -> CellUtils.deleteCell(jCell);
			case DELETE_ALL -> CellUtils.deleteAllGraph();
			case SAVE_CELL -> exportTable();
			case SHOW_CELL -> CellUtils.showTable(jCell);
			case IMPORT_FILE -> newTable(CurrentAction.ActionType.IMPORT_FILE);
			case CREATE_TABLE -> newTable(CurrentAction.ActionType.CREATE_TABLE);
			case OPEN_CONSOLE -> new Console();
			case OPEN_TEXT_EDITOR -> changeScreen();

			}

			if (btnClicked instanceof OperationButton btnOpClicked) {

				style = btnOpClicked.getStyle();
				((CreateOperationAction) currentActionRef.get()).setParent(null);

			}

		}

		CreateOperationAction opAction = null;

		if (e.getSource() == menuItemShow) {

			CellUtils.showTable(jCell);

		} else if (e.getSource() == menuItemInformations) {

			new CellInformationFrame(jCell);

		} else if (e.getSource() == menuItemExport) {

			new Export(Cell.getCells().get(jCell).getTree());

		} else if (e.getSource() == menuItemEdit) {

			((OperationCell) Cell.getCells().get(jCell)).editOperation(jCell);
			TreeUtils.recalculateContent(Cell.getCells().get(jCell));

		} else if (e.getSource() == menuItemRemove) {

			CellUtils.deleteCell(jCell);

		} else if (e.getSource() == menuItemSelection) {

			opAction = OperationType.SELECTION.getAction();
			style = OperationType.SELECTION.getDisplayName();

		} else if (e.getSource() == menuItemProjection) {

			opAction =  OperationType.PROJECTION.getAction();
			style = OperationType.PROJECTION.getDisplayName();

		} else if (e.getSource() == menuItemJoin) {

			opAction =  OperationType.JOIN.getAction();
			style = OperationType.JOIN.getDisplayName();

		} else if (e.getSource() == menuItemLeftJoin) {

			opAction = OperationType.LEFT_JOIN.getAction();
			style = OperationType.LEFT_JOIN.getDisplayName();

		} else if (e.getSource() == menuItemRightJoin) {

			opAction = OperationType.RIGHT_JOIN.getAction();
			style = OperationType.RIGHT_JOIN.getDisplayName();

		} else if (e.getSource() == menuItemCartesianProduct) {

			opAction = OperationType.CARTESIAN_PRODUCT.getAction();
			style = OperationType.CARTESIAN_PRODUCT.getDisplayName();

		} else if (e.getSource() == menuItemUnion) {

			opAction = OperationType.UNION.getAction();
			style = OperationType.UNION.getDisplayName();

		}else if (e.getSource() == menuItemIntersection) {

			opAction = OperationType.INTERSECTION.getAction();
			style = OperationType.INTERSECTION.getDisplayName();

		}else if (e.getSource() == menuItemSort) {

			opAction = OperationType.SORT.getAction();
			style = OperationType.SORT.getDisplayName();

		}

		if (opAction != null) {

			opAction.setParent(jCell);
			currentActionRef.set(opAction);

		}

		if (currentActionRef.get() != null && (opAction != null
				|| (btnClicked != null && currentActionRef.get().getType() == ActionType.CREATE_OPERATOR_CELL))) {

			ghostJCell = (mxCell) graph.insertVertex((mxCell) graph.getDefaultParent(), "ghost", style,
					MouseInfo.getPointerInfo().getLocation().getX() - MainFrame.getGraphComponent().getWidth(),
					MouseInfo.getPointerInfo().getLocation().getY() - MainFrame.getGraphComponent().getHeight(), 80, 30,
					style);
		}
		edgeRef.get().reset();

	}

	@Override
	public void mouseClicked(MouseEvent e) {

		jCell = (mxCell) MainFrame.getGraphComponent().getCellAt(e.getX(), e.getY());

		if (e.getButton() == MouseEvent.BUTTON3 && Cell.getCells().get(jCell) != null) {

			Cell cell = Cell.getCells().get(jCell);

			popupMenuJCell.add(menuItemShow);
			popupMenuJCell.add(menuItemInformations);
			popupMenuJCell.add(menuItemExport);
			popupMenuJCell.add(menuItemEdit);
			popupMenuJCell.add(menuItemOperations);
			popupMenuJCell.add(menuItemRemove);

			if (cell instanceof OperationCell opCell && !opCell.hasBeenInitialized()) {

				popupMenuJCell.remove(menuItemShow);
				popupMenuJCell.remove(menuItemOperations);
				popupMenuJCell.remove(menuItemEdit);
				popupMenuJCell.remove(menuItemExport);

			}
			if (cell instanceof TableCell || ((OperationCell) cell).getType() == OperationType.CARTESIAN_PRODUCT) {

				popupMenuJCell.remove(menuItemEdit);

			}
			if (cell.hasChild()) {

				popupMenuJCell.remove(menuItemOperations);

			}
			if (cell.hasError()) {

				popupMenuJCell.remove(menuItemShow);
				popupMenuJCell.remove(menuItemOperations);

				if (!cell.hasParents())
					popupMenuJCell.remove(menuItemEdit);

			}
			popupMenuJCell.show(MainFrame.getGraphComponent().getGraphControl(), e.getX(), e.getY());

		}

		ClickController.clicked(currentActionRef, jCell, edgeRef, e, ghostJCell);

		if (Cell.getCells().get(jCell) != null && e.getClickCount() == 2) {

			CellUtils.showTable(jCell);

		}

	}

	private void changeScreen() {

		setContentPane(textEditor);
		revalidate();

	}

	private void newTable(CurrentAction.ActionType action) {

		AtomicReference<Boolean> cancelServiceReference = new AtomicReference<>(false);

		TableCell tableCell = action == CurrentAction.ActionType.CREATE_TABLE
				? new FormFrameCreateTable(cancelServiceReference).getResult()
				: new FormFrameImportAs(cancelServiceReference).getResult();

		if (!cancelServiceReference.get()) {

			currentActionRef.set(new CreateTableAction(action, tableCell.getName(), tableCell.getStyle(), tableCell));
			saveTable(tableCell.getName(), tableCell.getTable());

		} else {

			if (tableCell != null)
				TreeUtils.deleteTree(tableCell.getTree());
			currentActionRef.set(null);

		}

	}

	public static void saveTable(String tableName, Table table) {

		boolean create = tables.keySet().stream().noneMatch(x -> x.equals(tableName));

		if (create) {
			tablesGraph.insertVertex((mxCell) tablesGraph.getDefaultParent(), null, tableName, 0, yTables, 80, 30,
					"table");

			tables.put(tableName, table);

			tablesPane.revalidate();

			yTables += 40;

		}

	}

	private void exportTable() {

		AtomicReference<Boolean> exitRef = new AtomicReference<>(false);

		if (!Cell.getCells().isEmpty())
			new FormFrameExportTable(exitRef);
	}

	@Override
	public void keyPressed(KeyEvent e) {

		if (e.getKeyCode() == KeyEvent.VK_S) {

			if (jCell != null)
				CellUtils.showTable(jCell);

		} else if (e.getKeyCode() == KeyEvent.VK_DELETE) {

			if (jCell != null) {

				CellUtils.deleteCell(jCell);
				currentActionRef.set(null);

			}

		} else if (e.getKeyCode() == KeyEvent.VK_E) {

			currentActionRef.set(new CurrentAction(CurrentAction.ActionType.EDGE));

		} else if (e.getKeyCode() == KeyEvent.VK_I) {

			newTable(CurrentAction.ActionType.IMPORT_FILE);

		} else if (e.getKeyCode() == KeyEvent.VK_X) {

			if (Cell.getCells().size() > 0)
				exportTable();

		} else if (e.getKeyCode() == KeyEvent.VK_C) {

			newTable(CurrentAction.ActionType.CREATE_TABLE);

		} else if (e.getKeyCode() == KeyEvent.VK_L) {

			System.out.println("--------------------------");
			System.out.println("Árvores: ");
			for (Integer i : trees.keySet()) {

				System.out.println(trees.get(i));

			}
			System.out.println();
			System.out.println();

		} else if (e.getKeyCode() == KeyEvent.VK_A) {

			if (jCell != null && Cell.getCells().get(jCell) != null) {

				System.out.println(jCell);

			}

		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {

		if (currentActionRef != null &&currentActionRef.get() != null)
			if (currentActionRef.get().getType() == CurrentAction.ActionType.CREATE_OPERATOR_CELL
					&& ghostJCell != null) {

				mxGeometry geo = ghostJCell.getGeometry();
				double dx = e.getX() - geo.getCenterX();
				double dy = e.getY() - geo.getCenterY();
				MainFrame.getGraph().moveCells(new Object[] { ghostJCell }, dx, dy);

			} else if (currentActionRef.get() instanceof CreateTableAction createTable) {

				mxGeometry geo = createTable.getTableCell().getJGraphCell().getGeometry();
				double dx = e.getX() - geo.getCenterX();
				double dy = e.getY() - geo.getCenterY();
				MainFrame.getGraph().moveCells(new Object[] { createTable.getTableCell().getJGraphCell() }, dx, dy);

			}

	}

	public static Map<Integer, Tree> getTrees() {
		return trees;
	}

	public static Map<String, Table> getTables() {
		return tables;
	}

	public static File getLastDirectory() {
		return lastDirectory;
	}

	public static void setLastDirectory(File newLastDirectory) {
		lastDirectory = newLastDirectory;
	}

	public static void putTableCell(Relation relation) {

		int x, y;
		if (relation.getCoordinates().isPresent()) {

			x = relation.getCoordinates().get().x();
			y = relation.getCoordinates().get().y();

		} else {

			x = (int) (Math.random() * 600);
			y = (int) (Math.random() * 600);

		}

		mxCell jTableCell = (mxCell) MainFrame.getGraph().insertVertex((mxCell) graph.getDefaultParent(), null,
				relation.getName(), x, y, 80, 30, "table");

		relation.setCell(new ImportFile(relation.getName() + ".head", jTableCell).getResult());

		saveTable(relation.getName(), relation.getCell().getTable());

	}

	public static void putOperationCell(OperationExpression operationExpression) {

		int x, y;
		if (operationExpression.getCoordinates().isPresent()) {

			x = operationExpression.getCoordinates().get().x();
			y = operationExpression.getCoordinates().get().y();

		} else {

			x = (int) (Math.random() * 600);
			y = (int) (Math.random() * 600);

		}

		OperationType type = operationExpression.getType();

		mxCell jCell = (mxCell) MainFrame.getGraph().insertVertex((mxCell) graph.getDefaultParent(), null,
				type.getDisplayNameAndSymbol(), x, y, 80, 30, type.getDisplayName());

		List<Cell> parents = new ArrayList<>();
		parents.add(operationExpression.getSource().getCell());

		if (operationExpression instanceof BinaryExpression binaryExpression)
			parents.add(binaryExpression.getSource2().getCell());

		operationExpression.setCell(new OperationCell(jCell, type, parents, operationExpression.getArguments()));

		OperationCell cell = operationExpression.getCell();

		cell.setAllNewTrees();

	}

}
