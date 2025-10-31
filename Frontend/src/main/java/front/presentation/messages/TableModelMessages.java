package front.presentation.messages;

import front.presentation.table_models.AbstractTableModel;

import java.util.ArrayList;
import java.util.List;
import logic.Message;


public class TableModelMessages extends AbstractTableModel<Message> implements javax.swing.table.TableModel {
    public TableModelMessages(int[] cols, List<Message> rows) {
        super(cols, rows);
        initFlags();
    }

    public static final int ID = 0;
    public static final int HAS_MESSAGES = 1;

    private List<Boolean> hasMessagesFlags = new ArrayList<>();

    public TableModelMessages(List<Message> rows) {
        this(new int[]{ ID, HAS_MESSAGES }, rows);
        initFlags();
    }

    @Override
    protected void initColNames() {
        colNames = new String[2];
        colNames[ID] = "Id";
        colNames[HAS_MESSAGES] = "Messages?";
    }

    private void initFlags() {
        hasMessagesFlags.clear();
        if (rows != null) {
            for (Message m : rows) {
                boolean has = false;
                try {
                    // default heuristic: consider a message present if content is non-empty
                    Object content = m.getContent();
                    has = content != null && !content.toString().trim().isEmpty();
                } catch (Exception ex) {
                    has = false;
                }
                hasMessagesFlags.add(has);
            }
        }
    }

    public void setRows(List<Message> newRows) {
        this.rows = newRows;
        initFlags();
        fireTableDataChanged();
    }

    @Override
    protected Object getPropetyAt(Message message, int col) {
        int realCol = cols[col];
        switch (realCol) {
            case ID:
                // show sender or some identifier
                return message.getSender();
            case HAS_MESSAGES:
                int idx = rows.indexOf(message);
                return (idx >= 0 && idx < hasMessagesFlags.size()) ? hasMessagesFlags.get(idx) : Boolean.FALSE;
            default:
                return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        int realCol = cols[columnIndex];
        switch (realCol) {
            case ID:
                return String.class;
            case HAS_MESSAGES:
                return Boolean.class;
            default:
                return Object.class;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        int realCol = cols[columnIndex];
        return realCol == HAS_MESSAGES;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        int realCol = cols[columnIndex];
        if (realCol == HAS_MESSAGES && rowIndex >= 0 && rowIndex < hasMessagesFlags.size()) {
            hasMessagesFlags.set(rowIndex, Boolean.TRUE.equals(aValue));
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @Override
    public int getColumnCount() {
        return colNames != null ? colNames.length : 2;
    }
}
