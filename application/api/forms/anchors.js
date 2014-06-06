(function() {
    var javaClass = Java.type("com.eas.client.forms.api.Anchors");
    javaClass.setPublisher(function(aDelegate) {
        return new P.Anchors(null, null, null, null, null, null, aDelegate);
    });
    
    /**
    * Component's layout anchors for AnchorsPane.
    * Two constraint values of three possible must be provided for X and Y axis, other constraints must be set to <code>null</code>.* Parameters values can be provided in pixels, per cents or numbers, e.g. '30px', '30' or 10%.
    * @param left a left anchor
    * @param width a width value
    * @param right a right anchor
    * @param top a top anchor
    * @param height a height value
    * @param bottom a bottom anchor
     * @namespace Anchors
    */
    P.Anchors = function (left, width, right, top, height, bottom) {

        var maxArgs = 6;
        var delegate = arguments.length > maxArgs ?
            arguments[maxArgs] : new javaClass(P.boxAsJava(left), P.boxAsJava(width), P.boxAsJava(right), P.boxAsJava(top), P.boxAsJava(height), P.boxAsJava(bottom));

        Object.defineProperty(this, "unwrap", {
            get: function() {
                return function() {
                    return delegate;
                };
            }
        });

        delegate.setPublished(this);
    };
})();