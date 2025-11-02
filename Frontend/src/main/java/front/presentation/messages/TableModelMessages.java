package front.presentation.messages;

import front.presentation.table_models.AbstractTableModel;
import logic.Message;

import java.util.ArrayList;
import java.util.List;

public class TableModelMessages extends javax.swing.table.AbstractTableModel{

    private final String[] colNames = { "Id", "Messages?" };
    private final List<String> users = new ArrayList<>();
    private final List<Boolean> hasMessagesFlags = new ArrayList<>();

    public TableModelMessages(List<String> initialUsers) {
        if (initialUsers != null) setUsers(initialUsers);
    }

    public void setUsers(List<String> newUsers) {
        users.clear();
        hasMessagesFlags.clear();

        if (newUsers != null) {
            users.addAll(newUsers);
            // Por defecto todos sin mensajes
            for (int i = 0; i < newUsers.size(); i++) {
                hasMessagesFlags.add(Boolean.FALSE);
            }
        }
        fireTableDataChanged();
    }

    public void setUserHasMessage(String userId, boolean hasMessage) {
        int idx = users.indexOf(userId);
        if (idx != -1) {
            hasMessagesFlags.set(idx, hasMessage);
            fireTableCellUpdated(idx, 1);
        }
    }

    public String getUserAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < users.size()) {
            return users.get(rowIndex);
        }
        return null;
    }


    @Override
    public int getRowCount() {
        return users.size();
    }

    @Override
    public int getColumnCount() {
        return colNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return colNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return switch (columnIndex) {
            case 0 -> users.get(rowIndex);
            case 1 -> hasMessagesFlags.get(rowIndex);
            default -> null;
        };
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // Solo la columna de "Messages?" debe ser editable
        return columnIndex == 1;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1 && rowIndex >= 0 && rowIndex < hasMessagesFlags.size()) {
            hasMessagesFlags.set(rowIndex, Boolean.TRUE.equals(aValue));
            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0 -> String.class;
            case 1 -> Boolean.class;
            default -> Object.class;
        };
    }
}
