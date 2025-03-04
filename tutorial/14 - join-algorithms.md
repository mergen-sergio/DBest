<div align="left">
    <a href="./13 - join-types.md">Previous</a>
</div>
<div align="right">
  <a href="./15 - using-boolean-expressions.md">Next</a>
</div>

# Join Algorithms 

## 1. **Nested Loop Join**
The **Nested Loop Join** algorithm evaluates each tuple from the outer side by searching for matching tuples on the inner side.

### **Key Points:**
- **Naive Strategy:** Scans all tuples on the inner side for each outer-side tuple, making it inefficient.
- **Optimized Usage:**  
  - Leverage an operator on the inner side to enhance lookup performance.  
  - Examples:
    - Using a data node indexed by the lookup column.
    - Applying a materialized operation that reorders data for efficient access.

### **Example:**
The illustration below demonstrates two query trees.  
- **Left Tree:** The `movie_cast` data node is on the outer side, while the `person` data node, indexed by `person_id`, is on the inner side. This setup is efficient since the index facilitates fast lookups for each tuple.  

<img src="assets/images/nested-loop-join.png" alt="Nested Loop Join Illustration" width="700"/>

- **Right Tree:** The sides are reversed, with `movie_cast` as the inner side. This is inefficient because:
  - The `movie_cast` node is indexed by both `movie_id` and `person_id`, but `person_id` is the secondary key.
  - Each lookup requires scanning the entire index for matches, resulting in high overhead.

### **Solutions for Inefficiency:**
1. **Switch the Sides:** Configure the `person` data node as the inner side to leverage its index (as in the left tree).  
2. **Add a Materialized Operation:** Connect `movie_cast` to a materialized operation that offers efficient lookups (as in the next example).  
3. **Use a Hash Join:** Replace the Nested Loop Join with a Hash Join for improved performance (as in the next example).

---

## 2. **Hash Join**
The **Hash Join** algorithm builds a hash table from the inner side using the join column(s) as the key. The outer side tuples are then matched against this hash table.

### **Key Points:**
- **Efficiency:** Performs better than Nested Loop Join for larger datasets.
- **Memory Usage:** Requires memory to store the hash table.
- **Equivalent Process:**  
  - Combines elements of a **Nested Loop Join**, **Hash operator**, and **Projection operator**.

### **Example:**
The figure below compares two equivalent approaches to Hash Join:  
- **Left Tree:** Directly builds a hash table from the inner side and uses it to match tuples from the outer side, avoiding repeated scans.  

<img src="assets/images/hash-join.png" alt="Hash Join Illustration" width="800"/>

- **Right Tree:** Projects relevant columns from the inner side, builds a hash table using these columns, and uses a Nested Loop Join for lookups. The **Hash operator** is a materialized operation that dynamically aligns its keys with the parent node (the join operator). Both trees achieve the same result, but the left tree is a simplified representation.

---

## 3. **Merge Join**
The **Merge Join** algorithm processes both sides of the join simultaneously in a single, forward-only scan, identifying matches as they are encountered.

### **Key Points:**
- **Prerequisite:** Both sides must be sorted by the join column(s).  
  - A **Sort operator** can be added to ensure the correct order.  
- **Pitfalls:** Unsuitable for unsorted data, as matches may be missed.
- **Efficiency:**
  - When pre-sorted:
    - It is the **most efficient join algorithm**.
    - Requires no additional memory.
    - Processes each side exactly once.

### **Example:**
The image below compares two query trees using Merge Join:  
- **Left Tree:** Joins `movie` and `movie_cast` nodes, both already sorted by the join column (`movie_id`). This is an optimal setup for Merge Join.  

<img src="assets/images/merge-join.png" alt="Merge Join Illustration" width="900"/>

- **Right Tree:** Joins `movie_cast` and `person` nodes, but requires a **Sort operator** for the inner side (`person`) to align tuples by the join column (`person_id`). When sorting is needed, alternative join strategies may offer better performance.

---

By understanding the nuances of these join algorithms, their optimal scenarios, and trade-offs, you can design efficient query plans to maximize relational query performance.

<br>

<div align="left">
    <a href="./13 - join-types.md">Previous</a>
</div>
<div align="right">
  <a href="./15 - using-boolean-expressions.md">Next</a>
</div>
