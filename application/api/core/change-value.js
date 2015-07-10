(function() {
    var className = "com.eas.client.changes.ChangeValue";
    var javaClass = Java.type(className);
    var space = this['-platypus-scripts-space'];
    space.putPublisher(className, function(aDelegate) {
        return new P.ChangeValue(aDelegate);
    });
    
    /**
     * Generated constructor.
     * @constructor ChangeValue ChangeValue
     */
    P.ChangeValue = function () {
        var maxArgs = 0;
        var delegate = arguments.length > maxArgs ?
              arguments[maxArgs] 
            : new javaClass();

        Object.defineProperty(this, "unwrap", {
            configurable: true,
            value: function() {
                return delegate;
            }
        });
        if(P.ChangeValue.superclass)
            P.ChangeValue.superclass.constructor.apply(this, arguments);
        delegate.setPublished(this);
        Object.defineProperty(this, "name", {
            get: function() {
                var value = delegate.name;
                return P.boxAsJs(value);
            }
        });
        if(!P.ChangeValue){
            /**
             * Name of changed property.
             * @property name
             * @memberOf ChangeValue
             */
            P.ChangeValue.prototype.name = '';
        }
        Object.defineProperty(this, "value", {
            get: function() {
                var value = delegate.value;
                return P.boxAsJs(value);
            }
        });
        if(!P.ChangeValue){
            /**
             * Value of changed property.
             * @property value
             * @memberOf ChangeValue
             */
            P.ChangeValue.prototype.value = {};
        }
    };
})();