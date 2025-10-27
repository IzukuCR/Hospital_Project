package presentation.table_models;

import logic.PrescriptionItem;

public class TableModelItems extends AbstractTableModel<PrescriptionItem> implements javax.swing.table.TableModel  {
    public TableModelItems(int[] cols, java.util.List<PrescriptionItem> rows) {
        super(cols, rows);
    }

    public TableModelItems(java.util.List<PrescriptionItem> rows) {
        this(new int[]{ ID, DOSAGE, QUANTITY }, rows);
    }

    public static final int ID = 0;
    //public static final int NAME = 1;
    public static final int DOSAGE = 1;
    public static final int QUANTITY = 2;

    @Override
    protected void initColNames() {
        colNames = new String[3];
        colNames[ID] = "ID";
        //colNames[NAME] = "Name";
        colNames[DOSAGE] = "Dosage";
        colNames[QUANTITY] = "Quantity";
    }

    @Override
    protected Object getPropetyAt(PrescriptionItem line, int col) {
        switch (cols[col]) {
            case ID:
                return line.getMedicineCode();
            case DOSAGE:
                return line.getDurationDays();
            case QUANTITY:
                return line.getQuantity();
            default:
                return "";
        }
    }

    public PrescriptionItem getRowAt(int row) {
        if (row >= 0 && row < rows.size()) {
            return rows.get(row);
        }
        return null;
    }

    public void setData(java.util.List<PrescriptionItem> newRows) {
        this.rows = (newRows != null) ? newRows : java.util.Collections.emptyList();
        fireTableDataChanged();
    }
}
