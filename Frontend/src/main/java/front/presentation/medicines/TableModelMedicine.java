package front.presentation.medicines;

import logic.Medicine;
import front.presentation.table_models.AbstractTableModel;

import java.util.List;

public class TableModelMedicine extends AbstractTableModel<Medicine> implements javax.swing.table.TableModel {
    public TableModelMedicine(int[] cols, List<Medicine> rows) {
        super(cols, rows);
    }

    public static final int CODE = 0;
    public static final int NAME = 1;
    public static final int PRESENTATION = 2;

    @Override
    protected void initColNames() {
        colNames = new String[3];
        colNames[CODE] = "Code";
        colNames[NAME] = "Name";
        colNames[PRESENTATION] = "Presentation";
    }

    @Override
    protected Object getPropetyAt(Medicine medicine, int col) {
        switch (cols[col]) {
            case CODE:
                return medicine.getCode();
            case NAME:
                return medicine.getName();
            case PRESENTATION:
                return medicine.getPresentation();
            default:
                return "";
        }
    }

    public Medicine getRowAt(int row) {
        if (row >= 0 && row < rows.size()) {
            return rows.get(row);
        }
        return null;
    }
}