(function() {
    var javaClass = Java.type("com.eas.client.model.application.ApplicationPlatypusModel");
    javaClass.setPublisher(function(aDelegate) {
        return new P.ApplicationPlatypusModel(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ApplicationPlatypusModel ApplicationPlatypusModel
     */
    P.ApplicationPlatypusModel = function ApplicationPlatypusModel() {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            value: function() {
                return delegate;
            }
        });
        if(ApplicationPlatypusModel.superclass)
            ApplicationPlatypusModel.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        var invalidatable = null;
        delegate.setPublishedCollectionInvalidator(function() {
            invalidatable = null;
        });
    }
    Object.defineProperty(P, "ApplicationPlatypusModel", {value: ApplicationPlatypusModel});
    Object.defineProperty(ApplicationPlatypusModel.prototype, "save", {
        value: function(arg0) {
            var delegate = this.unwrap();
            var value = delegate.save(P.boxAsJava(arg0));
            return P.boxAsJs(value);
        }
    });
    if(!ApplicationPlatypusModel){
        /**
         * Saves model data changes. Calls aCallback when done.
         * If model can't apply the changed, than exception is thrown.
         * In this case, application can call model.save() another time to save the changes.
         * @method save
         * @memberOf ApplicationPlatypusModel
         * If an application need to abort futher attempts and discard model data changes, than it can call model.revert().
        P.ApplicationPlatypusModel.prototype.save = function(arg0){};
    }
    Object.defineProperty(ApplicationPlatypusModel.prototype, "requery", {
        value: function(onSuccess, onFailure) {
            var delegate = this.unwrap();
            var value = delegate.requery(P.boxAsJava(onSuccess), P.boxAsJava(onFailure));
            return P.boxAsJs(value);
        }
    });
    if(!ApplicationPlatypusModel){
        /**
         * Requeries model data. Calls onSuccess callback when complete and onError callback if error occured.
         * @method requery
         * @memberOf ApplicationPlatypusModel
         */
        P.ApplicationPlatypusModel.prototype.requery = function(onSuccess, onFailure){};
    }
    Object.defineProperty(ApplicationPlatypusModel.prototype, "execute", {
        value: function(onSuccessCallback, onFailureCallback) {
            var delegate = this.unwrap();
            var value = delegate.execute(P.boxAsJava(onSuccessCallback), P.boxAsJava(onFailureCallback));
            return P.boxAsJs(value);
        }
    });
    if(!ApplicationPlatypusModel){
        /**
         * Refreshes the model, only if any of its parameters has changed.
         * @param onSuccessCallback the handler function for refresh data on success event (optional).
         * @param onFailureCallback the handler function for refresh data on failure event (optional).
         * @method execute
         * @memberOf ApplicationPlatypusModel
         */
        P.ApplicationPlatypusModel.prototype.execute = function(onSuccessCallback, onFailureCallback){};
    }
    Object.defineProperty(ApplicationPlatypusModel.prototype, "loadEntity", {
        value: function(queryId) {
            var delegate = this.unwrap();
            var value = delegate.loadEntity(P.boxAsJava(queryId));
            return P.boxAsJs(value);
        }
    });
    if(!ApplicationPlatypusModel){
        /**
         * Creates new entity of model, based on application query.
         * @param queryId the query application element ID.
         * @return a new entity.
         * @method loadEntity
         * @memberOf ApplicationPlatypusModel
         */
        P.ApplicationPlatypusModel.prototype.loadEntity = function(queryId){};
    }
})();