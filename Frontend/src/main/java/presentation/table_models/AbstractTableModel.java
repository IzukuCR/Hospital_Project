package main.java.presentation.table_models;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractTableModel<E> extends javax.swing.table.AbstractTableModel implements javax.swing.table.TableModel {
    protected List<E> rows;
    protected int[] cols;
    protected String[] colNames;

    public AbstractTableModel(int[] cols, List<E> rows){
        this.cols=cols;
        this.rows=rows;
        initColNames();
    }

    public int getColumnCount() {
        return cols.length;
    }

    public String getColumnName(int col){
        return colNames[cols[col]];
    }

    public Class<?> getColumnClass(int col){
        switch (cols[col]){
            default: return super.getColumnClass(col);
        }
    }

    public int getRowCount() {
        return rows == null ? 0 : rows.size();
    }

    public Object getValueAt(int row, int col) {
        if (rows == null || row >= rows.size()) {
            return null;
        }
        E e = rows.get(row);
        return getPropetyAt(e, col);
    }

    public E getRowAt(int row) {
        if (rows == null || row >= rows.size()) {
            return null;
        }
        return rows.get(row);
    }

    protected abstract Object getPropetyAt(E e, int col);



    protected abstract void initColNames();

    public void setRows(List<E> newRows) {
        this.rows = newRows != null ? newRows : new ArrayList<>();
        fireTableDataChanged();
    }

    public void clear() {
        this.rows = new ArrayList<>();
        fireTableDataChanged();
    }
}