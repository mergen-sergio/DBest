A join is one of the most imporant operations in relational table queries, where tables are connected through primary keys/foreign key relationships. 

Equi-joins

An equi-join contains a join predicate where the join terms are conjunctive equality conditions. It is the most common type of join, used to match a foreign key with its respective primary key value. In DBest, join operators support equi-joins only. When the join configuration pop-up is opened, the user needs to speficy which left side columns need to match the right side columns.

The image below shows an example, joining the movie and the movie_cast data nodes, using the Nested Loop Join algorithm. In this case, a single term is defined, comparing the movie_id column from both sides. However, more terms could be added if needed. 


Non-Equi joins


primary keys and foreign 
