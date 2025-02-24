In what follows we will present examples that show the types of queries can be expressed using DBest. Some queryes involve representations of SQL queries, while others explore different types of requests. 

The examples can be expressed in many different ways using DBest. We will explore a few os the possible paths to show the diversity of possible solutions and operators arrangements. 

Handling SQL subqueries

The most complex SQL queries involve subqueries, with nested processing and usage of correlated variables. Lets see which operatos DBest offer to support sub-queries and capture the nunances when subqueries are in use

Handling Exists/IN


The first SQL query finds movies that contains casting members. The query below shows how to handle the request using EXISTS. 
The subquery contains a correlated condition (m.movie_id = mc.movie_id). If satisfied, the outer side tuple is returned. 




SELECT * 
FROM movie m
WHERE EXISTS (
    SELECT 1 
    FROM movie_cast mc 
    WHERE m.movie_id = mc.movie_id
);

Since the correlated condition is by equality, the query can also be expressed with IN, as indicated below. The movie is deemed relevant if its movie_id appears in a list containing movie_ids thats are associated with cast members.


SELECT * 
FROM movie
WHERE movie_id IN  (
    SELECT movie_id 
    FROM movie_cast 
);

We also show two ways to express this query using DBest. The first uses a Nested Loop Semi Join to find matches directly into the movie_cast table. 
The second materializes the movie_cast tuples using a Hash Operator. The Nested Loop Semi Join that directs the movie_id lookups to the hash table. Alternatively, a Hash Left Semi join operator could be used instead of the Nested Loop Semi Join + Hash combination. 

space to describe the query PLANS.

Materialization is better when the number of lookups is high, as it avoids the cost of index accessing to solve the lookups.  
On the other hand, the pipelined version is prefered when the number of lookups is small, since it avoids the cost of materialization.
IN the past, SQL optimizers relied on materialization to solve IN subqueries, and a pipelined execeution to solve EXISTS subqueries. Now the optimizers are smart enough to decide which plan is better, regarless of how the SQL query was expressed.



The next SQL query finds movies released in the same year as Casablanca. It is expressed as an IN subquery, but EXISTS could be used as well. 

 SELECT * 
FROM movie
WHERE release_year IN (
    SELECT release_year 
    FROM movie 
    WHERE title = 'Casablanca' 
);

Below we show one possible way to expressed this query using DBest.  The Hash operator is used to materialize the movies called 'Casablanca' in a hash table indexed by year. A Nested Loop Semi Join finds the desired movies. 
Since few movies should be called 'Casablanca', the hash table cost is acceptable. 

space to describe the query PLAN.

The next SQL query finds movies released before Casablanca. This cannot be expressed using IN, since the correlated condition is range, not equality. 
However, EXISTS can express the correlated condition inside the subquery, as demosntrated below:

SELECT * 
FROM movie2 m1
WHERE EXISTS (
    SELECT 1 
    FROM movie2 m2 
    WHERE m2.title = 'Casablanca' 
    AND m1.release_year < m2.release_year
);

The example shows that the expressive power of EXISTS in superior that IN.

DBest can expresses this query as shown below. The solution is also based on materialization. However, instead of a hash, a Materialize operation is used. The filter over year accessed the materilized 'Casablanca' tuples to check the correlated condition. 
Note that the Nested Loop Semi Join was an empty condition, since it is up to the internal filter to do the range check. The filter is cheap, since it access a small materilized list of tuples, containing the 'Casablanca' movies.


space to describe the query PLAN.



