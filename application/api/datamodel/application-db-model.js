(function() {
    var javaClass = Java.type("com.eas.client.model.application.ApplicationDbModel");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ApplicationDbModel(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ApplicationDbModel ApplicationDbModel
     */
    P.ApplicationDbModel = function ApplicationDbModel() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ApplicationDbModel.superclass)
            ApplicationDbModel.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ApplicationDbModel", {value: ApplicationDbModel});
    Object.defineProperty(ApplicationDbModel.prototype, "save", {
        value: function(callback) {
            var delegate = this.unwrap();
            var value = delegate.save(P.boxAsJava(callback));
            return P.boxAsJs(value);
        }
    });
    if(!ApplicationDbModel){
        /**
         * Saves model data changes.
         * If model can't apply the changed data, than exception is thrown. In this case, application can call model.save() another time to save the changes.
         * If an application needs to abort futher attempts and discard model data changes, use <code>model.revert()</code>.
         * @param callback the function to be envoked after the data changes saved (optional)
         * @method save
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.save = function(callback){};
    }
    Object.defineProperty(ApplicationDbModel.prototype, "requery", {
        value: function(onSuccessCallback, onFailureCallback) {
            var delegate = this.unwrap();
            var value = delegate.requery(P.boxAsJava(onSuccessCallback), P.boxAsJava(onFailureCallback));
            return P.boxAsJs(value);
        }
    });
    if(!ApplicationDbModel){
        /**
         * Requeries the model data. Forses the model data refresh, no matter if its parameters has changed or not.
         * @param onSuccessCallback the handler function for refresh data on success event (optional).
         * @param onFailureCallback the handler function for refresh data on failure event (optional).
         * @method requery
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.requery = function(onSuccessCallback, onFailureCallback){};
    }
    Object.defineProperty(ApplicationDbModel.prototype, "createEntity", {
        value: function(sqlText, datasourceName) {
            var delegate = this.unwrap();
            var value = delegate.createEntity(P.boxAsJava(sqlText), P.boxAsJava(datasourceName));
            return P.boxAsJs(value);
        }
    });
    if(!ApplicationDbModel){
        /**
         * Creates new entity of model, based on passed sql query. This method works only in two tier components of a system.
         * @param sqlText SQL text for the new entity.
         * @param dbId the concrete database ID (optional).
         * @return an entity instance.
         * @method createEntity
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.createEntity = function(sqlText, datasourceName){};
    }
    Object.defineProperty(ApplicationDbModel.prototype, "executeSql", {
        value: function(sqlText, datasourceName) {
            var delegate = this.unwrap();
            var value = delegate.executeSql(P.boxAsJava(sqlText), P.boxAsJava(datasourceName));
            return P.boxAsJs(value);
        }
    });
    if(!ApplicationDbModel){
        /**
         * Executes a SQL query against specific datasource. This method works only in two tier components of a system.
         * @param sqlText SQL text for the new entity.
         * @param dbId Optional. the concrete database ID (optional).
         * @return an entity instance.
         * @method executeSql
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.executeSql = function(sqlText, datasourceName){};
    }
    Object.defineProperty(ApplicationDbModel.prototype, "execute", {
        value: function(onSuccessCallback, onFailureCallback) {
            var delegate = this.unwrap();
            var value = delegate.execute(P.boxAsJava(onSuccessCallback), P.boxAsJava(onFailureCallback));
            return P.boxAsJs(value);
        }
    });
    if(!ApplicationDbModel){
        /**
         * Refreshes the model, only if any of its parameters has changed.
         * @param onSuccessCallback the handler function for refresh data on success event (optional).
         * @param onFailureCallback the handler function for refresh data on failure event (optional).
         * @method execute
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.execute = function(onSuccessCallback, onFailureCallback){};
    }
    Object.defineProperty(ApplicationDbModel.prototype, "loadEntity", {
        value: function(queryId) {
            var delegate = this.unwrap();
            var value = delegate.loadEntity(P.boxAsJava(queryId));
            return P.boxAsJs(value);
        }
    });
    if(!ApplicationDbModel){
        /**
         * Creates new entity of model, based on application query.
         * @param queryId the query application element ID.
         * @return a new entity.
         * @method loadEntity
         * @memberOf ApplicationDbModel
         */
        P.ApplicationDbModel.prototype.loadEntity = function(queryId){};
    }
})();