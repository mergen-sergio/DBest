# Join Types

There are several types of joins, each serving different purposes: **Inner Join**, **Outer Join**, **Semi Join**, and **Anti Join**.

---

## 1. **Inner Join**
The **Inner Join** is the most commonly used join type. For each tuple on the outer side, it finds matching tuples on the inner side. The result is a tuple formed by concatenating the outer-side tuple with the matching inner-side tuple.

---

## 2. **Outer Join**
The **Outer Join** operates similarly to the Inner Join but includes unmatched tuples:
- **Left Outer Join:** Retrieves all tuples from the outer side. If no match is found on the inner side, the tuple is complemented with `NULL` values for the inner side.
- **Right Outer Join:** Retrieves all tuples from the inner side. If no match is found on the outer side, the tuple is complemented with `NULL` values for the outer side.

---

## 3. **Semi Join**
The **Semi Join** retrieves a tuple from one side only if at least one match exists on the other side. It does not return tuples from the other side.

- **Optimized Behavior:** The algorithm only checks if a match exists, avoiding the overhead of locating and returning all matches.
- **Left Semi Join:** Returns tuples from the outer side.
- **Right Semi Join:** Returns tuples from the inner side.

---

## 4. **Anti Join**
The **Anti Join** retrieves tuples from one side when no matches exist on the other side.

- **Optimized Behavior:** The algorithm only verifies the absence of matches, without retrieving any.
- **Left Anti Join:** Returns tuples from the outer side.
- **Right Anti Join:** Returns tuples from the inner side.
