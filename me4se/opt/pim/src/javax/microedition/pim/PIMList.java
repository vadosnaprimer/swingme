package javax.microedition.pim;

public interface PIMList {

  void addCategory(java.lang.String category) throws PIMException;

  // Adds the provided category to the PIM list.
  void close() throws PIMException;

  // Closes the list, releasing any resources for this list.
  void deleteCategory(java.lang.String category, boolean deleteUnassignedItems) throws PIMException;

  // Deletes the indicated category from the PIM list.
  java.lang.String getArrayElementLabel(int stringArrayField, int arrayElement);

  // Returns a String label associated with the given array element.
  java.lang.String getAttributeLabel(int attribute);

  // Returns a String label associated with the given attribute.
  java.lang.String[] getCategories();

  // Returns the categories defined for the PIM list.
  int getFieldDataType(int field);

  // Returns an int representing the data type of the data associated with the
  // given field.
  java.lang.String getFieldLabel(int field);

  // Returns a String label associated with the given field.
  java.lang.String getName();

  // Provides the name of the list.
  int[] getSupportedArrayElements(int stringArrayField);

  // Returns an integer array containing all of the supported elements of a
  // string array for the given field.
  int[] getSupportedAttributes(int field);

  // Returns an integer array containing all of the supported attributes for the
  // given field.
  int[] getSupportedFields();

  // Gets all fields that are supported in this list.
  boolean isCategory(java.lang.String category);

  // Returns indication of whether the given category is a valid existing
  // category for this list.
  boolean isSupportedArrayElement(int stringArrayField, int arrayElement);

  // Indicates whether or not the given element in a array is supported for the
  // indicated field in this PIM list.
  boolean isSupportedAttribute(int field, int attribute);

  // Indicates whether or not the given attribute is supported in this PIM list
  // for the indicated field.
  boolean isSupportedField(int field);

  // Indicates whether or not the given field is supported in this PIM list.
  java.util.Enumeration items() throws PIMException;

  // Return an Enumeration of all items in the list.
  java.util.Enumeration items(PIMItem matchingItem) throws PIMException;

  // Return an Enumeration of all items in the list that contain fields that
  // match all of those fields specified in the given matching item.
  java.util.Enumeration items(java.lang.String matchingValue) throws PIMException;

  // Return an Enumeration of all items in the list that contain at least one
  // String field data value that matches the string value provided.
  java.util.Enumeration itemsByCategory(java.lang.String category) throws PIMException;

  // Returns an enumeration of all items in the PIM list that match the provided
  // category.
  int maxCategories();

  // Returns the maximum number of categories that this list can have.
  int maxValues(int field);

  // Indicates the total number of data values that a particular field supports
  // in this list.
  void renameCategory(java.lang.String currentCategory, java.lang.String newCategory) throws PIMException;
  // Renames a category from an old name to a new name.
}