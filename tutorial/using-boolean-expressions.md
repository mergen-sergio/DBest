## Boolean Expressions and the Filter Operator

A **boolean expression** is a structure composed of smaller expressions that evaluate to either `true` or `false`. 

### Combining Boolean Expressions
Boolean expressions can be combined using logical operators such as:
- **AND**
- **OR**

### Boolean Expressions in Filter Operators
One common use of boolean expressions is within the **Filter Operator**, which returns only the rows that satisfy the boolean expression.  
- The boolean expression is defined by editing the properties of the filter operator.

---

### Boolean Expression Editor
The Boolean Expression Editor allows users to create and modify expressions, including:
- **AND Expressions:** Combine multiple conditions, all of which must be `true`.
- **OR Expressions:** Combine multiple conditions, where at least one must be `true`.
- **Atomic Expressions:** Involve a single comparison between two elements, such as:
  - **Constant values** (e.g., `5`, `'John'`)
  - **Dynamic values** (e.g., values from columns)

---

### Example: Creating a Boolean Expression
The example below demonstrates how to create a boolean expression for a filter operator. The expression includes:
- An **AND expression** that combines two atomic expressions.

Additional atomic expressions or sub-expressions can be added by clicking the appropriate button in the editor.

---

### Example: Editing an Atomic Expression
The example also shows how to edit an existing atomic expression. The user changes the compared elements, such as modifying constant values or selecting different columns for comparison.
