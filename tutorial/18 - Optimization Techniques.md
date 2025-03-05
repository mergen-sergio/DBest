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


