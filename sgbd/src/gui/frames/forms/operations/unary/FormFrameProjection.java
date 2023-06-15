package gui.frames.forms.operations.unary;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Objects;

import javax.swing.*;

import com.mxgraph.model.mxCell;

import entities.Column;
import gui.frames.forms.operations.FormFrameOperation;
import gui.frames.forms.operations.IFormFrameOperation;

public class FormFrameProjection extends FormFrameOperation implements ActionListener, IFormFrameOperation {

	private final JButton btnAdd = new JButton("Adicionar");;
	private final JButton btnRemove = new JButton("Remover colunas");
	private final JButton btnAddAll = new JButton("Adicionar todas");
	private final JTextArea textArea = new JTextArea();

	public FormFrameProjection(mxCell jCell) {

		super(jCell);

		initializeGUI();

	}

	private void initializeGUI() {

		btnReady.addActionListener(this);
		btnCancel.addActionListener(this);

		textArea.setPreferredSize(new Dimension(300,300));
		textArea.setEditable(false);

		btnAdd.addActionListener(this);
		btnRemove.addActionListener(this);
		btnAddAll.addActionListener(this);

		addExtraComponent(btnAdd, 0, 2, 1, 1);
		addExtraComponent(btnAddAll, 1, 2, 1, 1);
		addExtraComponent(btnRemove, 2, 2, 1, 1);
		addExtraComponent(new JScrollPane(textArea), 0, 3, 3, 3);

		setPreviousArgs();

		pack();
		setLocationRelativeTo(null);
		setVisible(true);

	}

	@Override
	protected void setPreviousArgs() {

		if(!previousArguments.isEmpty()){

			for(String element : previousArguments){

				String groupByColumnName = Column.removeSource(element);
				String groupByColumnSource = Column.removeName(element);

				comboBoxSource.setSelectedItem(groupByColumnSource);
				comboBoxColumn.setSelectedItem(groupByColumnName);

				if (comboBoxColumn.getItemCount() > 0)
					updateColumns();

			}

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == btnAdd) {

			if (comboBoxColumn.getItemCount() > 0)
				updateColumns();

		} else if (e.getSource() == btnRemove) {

			textArea.setText("");

			restrictedColumns.clear();
			comboBoxColumn.removeAllItems();

			parent1.getColumnNames().forEach(comboBoxColumn::addItem);

		} else if(e.getSource() == btnCancel){

			closeWindow();

		}else if (e.getSource() == btnReady) {
			
			arguments.addAll(List.of(textArea.getText().split("\n")));
			arguments.remove(0);
			btnReady();

		}  else if (e.getSource() == btnAddAll) {

			while (comboBoxColumn.getItemCount() != 0) {

				updateColumns();

			}

		}
	}

	private void updateColumns(){

		String column = Objects.requireNonNull(comboBoxSource.getSelectedItem()).toString()+
				"."+
				Objects.requireNonNull(comboBoxColumn.getSelectedItem()).toString();
		String textColumnsPicked = textArea.getText() + "\n" + column;
		restrictedColumns.add(column);
		comboBoxColumn.removeItemAt(comboBoxColumn.getSelectedIndex());
		textArea.setText(textColumnsPicked);

	}

}
