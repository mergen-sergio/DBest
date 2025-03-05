## Optimization Techniques

When planning a query, two key factors should be considered:  
1. **Reducing memory consumption**  
2. **Minimizing I/O transfers from disk to memory**  

Transferring pages from disk is an expensive operation, especially when using **magnetic disks**. This is because locating data involves **mechanical movement**—spinning platters and repositioning the read head.  
Even with **SSDs**, where access time is significantly lower, avoiding disk I/O is still beneficial. Keeping data in **main memory** prevents unnecessary **system calls**, which trigger internal OS operations and add overhead.  

On the other hand, excessive memory usage can lead to **wasting available RAM**, forcing the OS to **swap** data to disk. This creates a **vicious cycle**, as swapping brings back the **I/O bottleneck**—moving data **to and from disk**.  

Some query optimization techniques **prioritize reducing memory consumption**, while others focus on **minimizing disk I/O**. When designing a query plan, it's crucial to determine **Which factor is more important** for the specific query.  
and **Whether a balanced approach** between both criteria is necessary.  

### Fundamental Query Optimization Techniques  
Database literature highlights three basic techniques for optimization:  
- **Pushing down filters** – Apply filtering as early as possible to reduce the amount of data processed.  
- **Combining join operations** – Optimize join execution order to reduce intermediate result size.  
- **Removing unnecessary columns early** – Select only relevant columns at the start to minimize memory usage.  

While many other strategies exist, these three provide a solid foundation for understanding **how to optimize query execution** effectively.  


## Combining Join Operators

When defining a **join operator**, it is crucial to decide which component (**table/subquery subtree**) should be on the **outer** or **inner** side. This decision depends on the **join algorithm** used and the **optimization criteria** prioritized.  

### Nested Loop Join 
For a **nested loop join**, reducing the **outer side** minimizes the number of lookups needed on the **inner side**. Consider the two query trees below, which join `movie` and `movie_cast`:  

*(Image goes here)*  

- **Left tree:** `movie` is the **outer** table.  
- **Right tree:** `movie_cast` is the **outer** table.  

Since `movie` is the **smaller** table, the **left tree** is preferable as it reduces the number of lookups on `movie_cast`.  

However, **table size is not the only factor**—the **inner table must support efficient lookups** for the join to be optimal. This is often **more important** than table size alone.  

Consider a **join between `person` and `movie_cast`**:  

*(Image goes here)*  

- **Size-based decision:** `person` is smaller, so it should be **outer**.  
- **Index-based decision:** `movie_cast` does **not** have an efficient index on `person_id`. It is indexed by **(movie_id, person_id)**, making lookups by `person_id` inefficient.  

Because `person_id` is **not a leading index column**, it cannot be queried efficiently. **In this case, `movie_cast` should be the outer table**, as `person` allows efficient lookups using its primary key.  

If we need to keep `movie` as the **outer** table, we can **create an index on the foreign key `movie_cast.person_id`**. This index enables efficient lookups and improves join performance:  

*(Image goes here)*  

- **New index:** `fk_mca_person` on `movie_cast.person_id`.  
- **Lookup process:**  
  - The index retrieves the **primary key** of `movie_cast` (`movie_id`, `person_id`).  
  - A secondary lookup efficiently fetches the corresponding row.  

This example highlights the **importance of indexing foreign keys** to enable **efficient join execution** and **query optimization**.  

### Hash Join 

## Optimizing Hash Joins  

For a **hash join**, reducing the **inner side** minimizes the amount of memory needed to build the hash table. Consider the two query trees below, which join `movie` and `movie_cast`:  

*(Image goes here)*  

- **Left tree:** `movie` is the **outer** table.  
- **Right tree:** `movie` is the **inner** table.  

Since `movie` is the **smaller** table, the **right tree** is preferable because it reduces memory consumption when constructing the hash table.  

### Choosing Between Hash Join and Nested Loop Join  
The example above could also be efficiently solved using a **nested loop join** by leveraging an **index** instead of a hash table. The **nested loop join** would reduce memory usage, while the **hash join** offers slightly faster lookups due to its **O(1) search cost**. This demonstrates how choosing a **join algorithm** influences the **table placement** in the query plan.  

### When Hash Join is the Best Choice  
In some cases, a **hash join is the only efficient option**—specifically, when **no indexes are available** on the join condition.  

For example, consider a query that finds movies whose **title matches a character name** in any movie:  

```sql
SELECT m1.title, mc.character_name
FROM movie m1
JOIN movie_cast mc ON m1.title = mc.character_name;
```

If there are **no indexes** on `title` or `character_name`, **other join algorithms become inefficient**. The **hash join** is the best choice in this scenario, as it avoids expensive sequential scans and reduces the number of comparisons.

## Pushing down filters

## Pushing Down Filters  

The concept behind this strategy is straightforward: **applying filters as early as possible reduces the amount of work needed for the rest of the query execution**.  

### Example: Filtering `movie_cast` by Year and `cast_order`  

Consider the queries below, which retrieve **only `movie_cast` entries from the year 2010 where `cast_order` is greater than 200**. These are **highly selective** filters—few movies were released in 2010, and even fewer have more than 200 cast members.  

*(Image goes here)*  

Both queries use the **nested loop join** strategy:  

- **Left query:** Filters are applied **after** the join.  
  - This is inefficient because the join processes **many irrelevant rows** that are later discarded.  

- **Right query:** Filters are **pushed down** before the join.  
  - This **eliminates irrelevant rows early**, reducing the number of tuples processed by the join.  

The right query **chooses `movie_cast` as the outer table** because the filter on `cast_order` is more selective than the filter on `year`. This highlights how **the presence of filters can influence join ordering**.  

Notice that in the optimized query, the **join condition disappears** from the join operator and is instead applied as a **filter on `movie`**. This happens because it is the operator **connected** to `movie` that drives the lookup. If the filter on `year` were applied directly to `movie`, it would trigger a **full scan** of `movie`, negating the benefits of filtering early. To prevent this, the **join condition is transformed into a filter on `movie`**, ensuring that only relevant rows are fetched.  As a result, the **join operator itself has an empty condition**, since **the filtering is already handled before the join occurs**.  

### Key Takeaways  
✅ **Apply filters as early as possible** to reduce unnecessary computations.  
✅ **Filters can influence join ordering**, changing which table is placed on the outer side.  
✅ **Join predicates may be rewritten as filters** to optimize query execution.  




If the hash join was used instead, it would be better to place the filter in the inner side, as it helps reducing the memory consumption. 

Pushing down filter is more important


