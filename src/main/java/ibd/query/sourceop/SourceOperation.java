/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ibd.query.sourceop;

import ibd.query.Operation;
import ibd.query.ReferedDataSource;


/**
 * A source operation is a leaf node. Its provides direct access to a data source (a table).
 * The data source has as alias. The alias is used by other operations to identify rows that come from a specific data source
 * @author Sergio
 */
public abstract class SourceOperation extends Operation{
    
    //the alias of the data source accessed by this source operation
    String dataSourceAlias;
    
    /**
     *
     * @param dataSourceAlias the alias of the data source accessed by this source operation
     */
    public SourceOperation(String dataSourceAlias){
        this.dataSourceAlias = dataSourceAlias;
        
    }
    
    public void asName(String newName){
        this.dataSourceAlias = newName;
    }
    
    /**
     * 
     * @return the alias of the data source accessed by this source operation
     */
    public String getDataSourceAlias() {
    return dataSourceAlias;
    }

    /**
     * {@inheritDoc }
     *  Source operations are leaf nodes and reach a single data source. 
     * @throws Exception
     */
    @Override
    public void setDataSourcesInfo() throws Exception {
        dataSources = new ReferedDataSource[1];
        dataSources[0] = new ReferedDataSource();
        dataSources[0].alias = dataSourceAlias;
    }
    
    
   
    
    /**
     *
     * @return
     */
    @Override
     public String toString(){
         return "["+dataSourceAlias+"]";
     }
    
}
