Creating a Query Tree

A query tree is composed by data nodes and operator nodes. Data nodes are located at the left panel. Operators are located in the right panel.  Click on the selected operator and move the mouse to the editor where you want the operator to be placed. Conversely, data nodes are placed into the editor by drag and drop.  

Nodes are connected by using the edge menu item, at the bottom menu. Select the source node, then click on the edge button, then the target node. Once a connection is stablished, a window pop ups where the user can inform the operator properties. With binary operators, the window pops up only after its two child nodes are connected. Optionally, the properties window can be oppened by right clicking over the node and choosing the 'Edit' menu item.

The properties depends on the type of operator. For instance, the projection operator asks the user to define the columns to be projected. A join operator asks the user to define the join predicate.

The screenshot below shows the window that asks the user the inform the columns to be returned by the projection operator. 

The property window of a node displays information about the schema of the connected nodes. For instance, the projetion operador shows the columns that are accessible by the node that is connected to the operators. A join operator divides columns into two groups:left and right. The left group shows columns that are accessible by the node thats is connected as the left child. The right group shows columns that are accessible by the node that is connected as the right child. 

If a child node is disconnected, or replaced by a node that has a different schema, the operator properties may become invalid. The node label is coolred in red to denote the existence of invalid properties. 
