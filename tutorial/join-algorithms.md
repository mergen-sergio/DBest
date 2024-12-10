# Join Algorithms 

## 1. **Nested Loop Join**
The **Nested Loop Join** algorithm evaluates each tuple from the outer side by searching for matching tuples on the inner side.

### **Key Points:**
- **Naive Strategy:** Scans all tuples on the inner side for each tuple on the outer side, which is highly inefficient.
- **Optimized Usage:**  
  - Use an operator on the inner side to improve lookup performance.  
  - Examples:
    - A data node indexed by the lookup column.
    - A materialized operation using the lookup column as the search key.

---

## 2. **Hash Join**
The **Hash Join** algorithm builds a hash table by scanning the inner side once, storing all inner-side tuples using the **lookup column(s)** as the key. Matches for outer-side tuples are then quickly found by querying the hash table.

### **Key Points:**
- **Efficiency:** More efficient than the Nested Loop Join.
- **Memory Usage:** Requires memory to store the hash table.
- **Equivalent Process:** Functions similarly to a combination of:
  - A **Nested Loop Join**.
  - A **Hash operator**.
  - A **Projection operator**.

The image below demonstrates this equivalence.

![Hash Join Illustration](assets/images/hash-join.png)

---

## 3. **Merge Join**
The **Merge Join** algorithm traverses both sides of the join in a forward-only direction, identifying matches as tuples are read.

### **Key Points:**
- **Prerequisite:** Both sides must be sorted by the join condition columns.
  - A **Sort operator** can be added to ensure the required order.
- **Pitfalls:** If tuples are unsorted, matches may be missed.
- **Efficiency:** 
  - When tuples are already ordered:
    - It is the **most efficient algorithm**.
    - Consumes no additional memory.
    - Scans each side only once.

The image below illustrates a query tree where the inner side required a **Sort operator** because tuples were not sorted by the join column (`person_id`). If a **Sort operator** is necessary, it may be better to use a different join algorithm.

![Merge Join Illustration]((assets/images/merge-join.png)

---

By understanding these join algorithms, their strengths, limitations, and optimization opportunities, you can enhance the performance of relational table queries.
