package presentation.pharmacists;

import logic.Pharmacist;
import presentation.table_models.AbstractTableModel;

import java.util.List;

public class TableModelPharmacist extends AbstractTableModel<Pharmacist> implements javax.swing.table.TableModel {
    public TableModelPharmacist(int[] cols, List<Pharmacist> rows) {
        super(cols, rows);
    }

    public static final int ID = 0;
    public static final int NAME = 1;
    public static final int PHARMACY = 2;
    public static final int PASS = 3;

    @Override
    protected void initColNames() {
        colNames = new String[4];
        colNames[ID] = "ID";
        colNames[NAME] = "Name";
        colNames[PHARMACY] = "Pharmacy";
        colNames[PASS] = "Password";
    }

    @Override
    protected Object getPropetyAt(Pharmacist pharmacist, int col) {
        switch (cols[col]) {
            case ID:
                return pharmacist.getId();
            case NAME:
                return pharmacist.getName();
            case PHARMACY:
                return pharmacist.getShift();
            case PASS:
                return "•••"; // Ocultar password
            default:
                return "";
        }
    }

    public Pharmacist getRowAt(int row) {
        if (row >= 0 && row < rows.size()) {
            return rows.get(row);
        }
        return null;
    }
}