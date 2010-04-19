package javax.microedition.pim;

public interface PIMList {
    String UNCATEGORIZED = null;

    public String getName();

    public void close() throws PIMException;

    public java.util.Enumeration items() throws PIMException;

    public java.util.Enumeration items(PIMItem matchingItem)
            throws PIMException;

    public java.util.Enumeration items(String matchingValue)
            throws PIMException;

    public java.util.Enumeration itemsByCategory(String category)
            throws PIMException;

    public String[] getCategories() throws PIMException;

    public boolean isCategory(String category) throws PIMException;

    public void addCategory(String category) throws PIMException;

    public void deleteCategory(String category, boolean deleteUnassignedItems)
            throws PIMException;

    public void renameCategory(String currentCategory, String newCategory)
            throws PIMException;

    public int maxCategories();

    public boolean isSupportedField(int field);

    public int[] getSupportedFields();

    public boolean isSupportedAttribute(int field, int attribute);

    public int[] getSupportedAttributes(int field);

    public boolean isSupportedArrayElement(int stringArrayField,
            int arrayElement);

    public int[] getSupportedArrayElements(int stringArrayField);

    public int getFieldDataType(int field);

    public String getFieldLabel(int field);

    public String getAttributeLabel(int attribute);

    public String getArrayElementLabel(int stringArrayField, int arrayElement);

    public int maxValues(int field);

    public int stringArraySize(int stringArrayField);
}
