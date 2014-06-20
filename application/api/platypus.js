(function() {
    load("classpath:internals.js");
    //this === global;
    var global = this;
    var oldP = global.P;
    global.P = {};
    Object.defineProperty(global.P, "restore", {
        value: function() {
            var ns = global.P;
            global.P = oldP;
            return ns;
        }
    });
    /*
     global.P = this; // global scope of api - for legacy applications
     global.P.restore = function() {
     throw "Legacy api can't restore the global namespace.";
     };
     */

    // core imports
    var ExecutorsClass = Java.type("java.util.concurrent.Executors");
    var Lock = Java.type("java.util.concurrent.locks.ReentrantLock");
    var EngineUtilsClass = Java.type("jdk.nashorn.api.scripting.ScriptUtils");
    var JavaArrayClass = Java.type("java.lang.Object[]");
    var FileClass = Java.type("java.io.File");
    var JavaDateClass = Java.type("java.util.Date");
    var LoggerClass = Java.type("java.util.logging.Logger");
    var RowClass = Java.type("com.bearsoft.rowset.Row");
    var FieldsClass = Java.type("com.bearsoft.rowset.metadata.Fields");
    var IDGeneratorClass = Java.type("com.bearsoft.rowset.utils.IDGenerator");
    var RowsetJSAdapterClass = Java.type("com.bearsoft.rowset.events.RowsetJSAdapter");
    var RowsComparatorClass = Java.type("com.bearsoft.rowset.sorting.RowsComparator");
    var ScriptTimerTaskClass = Java.type("com.eas.client.scripts.ScriptTimerTask");
    var ScriptedResourceClass = Java.type("com.eas.client.scripts.PlatypusScriptedResource");
    var DeamonThreadFactoryClass = Java.type("com.eas.concurrent.DeamonThreadFactory");
    var ScriptUtilsClass = Java.type('com.eas.script.ScriptUtils');
    var FileUtilsClass = Java.type("com.eas.util.FileUtils");
    var MD5GeneratorClass = Java.type("com.eas.client.login.MD5Generator");
    var TemplateLoaderClass = Java.type('com.eas.client.reports.store.Dom2ReportDocument');
    var ModelLoaderClass = Java.type('com.eas.client.scripts.store.Dom2ModelDocument');
    var TwoTierModelClass = Java.type('com.eas.client.model.application.ApplicationDbModel');
    var ThreeTierModelClass = Java.type('com.eas.client.model.application.ApplicationPlatypusModel');
    var CreateRequestClass = Java.type('com.eas.client.threetier.requests.CreateServerModuleRequest');
    // gui imports
    var KeyEventClass = Java.type("java.awt.event.KeyEvent");
    var SwingUtilitiesClass = Java.type("javax.swing.SwingUtilities");
    var FileChooserClass = Java.type("javax.swing.JFileChooser");
    var ColorChooserClass = Java.type("javax.swing.JColorChooser");
    var OptionPaneClass = Java.type("javax.swing.JOptionPane");
    var ColorClass = Java.type("com.eas.gui.ScriptColor");
    var FormClass = Java.type("com.eas.client.forms.Form");
    var FormLoaderClass = Java.type('com.eas.client.forms.store.Dom2FormDocument');
    var IconResourcesClass = Java.type("com.eas.client.forms.IconResources");
    var HorizontalPositionClass = Java.type("com.eas.client.forms.api.HorizontalPosition");
    var VerticalPositionClass = Java.type("com.eas.client.forms.api.VerticalPosition");
    var OrientationClass = Java.type("com.eas.client.forms.api.Orientation");
    //
    Object.defineProperty(P, "HTML5", {value: "HTML5 client"});
    Object.defineProperty(P, "J2SE", {value: "Java SE client/server environment"});
    Object.defineProperty(P, "agent", {value: P.J2SE});
    /** 
     * invokeLater - invokes given function in AWT event thread
     */
    Function.prototype.invokeLater = function() {
        var func = this;
        var args = arguments;
        SwingUtilitiesClass.invokeLater(function() {
            func.apply(func, args);
        });
    };
    /** 
     * Thread - schedules given function in the pool thread
     */
    Function.prototype.invokeDelayed = function() {
        var func = this;
        var args = arguments;
        if (!args || !args.length || args.length < 1)
            throw "invokeDelayed needs at least 1 argument - timeout value.";
        var userArgs = [];
        for (var i = 1; i < args.length; i++) {
            userArgs.push(args[i]);
        }
        ScriptTimerTaskClass.schedule(function() {
            SwingUtilitiesClass.invokeLater(function() {
                try {
                    func.apply(func, userArgs);
                } catch (e) {
                    P.Logger.severe(e);
                }
            });
        }, args[0]);
        // HTML5 client doesn't support cancel feature and so, we don't support it too.
    };
    var fixedThreadPool = null;
    /** 
     * Thread - schedules given function in the thread pool
     */
    Function.prototype.invokeBackground = function() {
        if (!fixedThreadPool) {
            fixedThreadPool = ExecutorsClass.newFixedThreadPool(10, new DeamonThreadFactoryClass());
        }
        var func = this;
        var args = arguments;
        fixedThreadPool.execute(function() {
            func.apply(func, args);
        });
    };
    /** 
     * Swing invokeAndWait - invokes given function in AWT event thread
     * and waits for it's completion
     */
    Function.prototype.invokeAndWait = function() {
        var func = this;
        var args = arguments;
        SwingUtilitiesClass.invokeAndWait(function() {
            func.apply(func, args);
        });
    };
    load("classpath:deps.js");

    /**
     * @static
     * @param {type} deps
     * @param {type} aOnSuccess
     * @param {type} aOnFailure
     * @returns {undefined}
     */
    function require(deps, aOnSuccess, aOnFailure) {
        try {
            if (deps) {
                if (Array.isArray(deps)) {
                    for (var i = 0; i < deps.length; i++) {
                        ScriptedResourceClass.executeScriptResource(deps[i]);
                    }
                } else {
                    ScriptedResourceClass.executeScriptResource(deps);
                }
            }
            if (aOnSuccess) {
                aOnSuccess();
            }
        } catch (e) {
            if (aOnFailure)
                aOnFailure(e);
            else
                throw e;
        }
    }
    Object.defineProperty(P, "require", {value: require});
    function extend(Child, Parent) {
        var F = function() {
        };
        F.prototype = Parent.prototype;
        Child.prototype = new F();
        Child.prototype.constructor = Child;
        Child.superclass = Parent.prototype;
    }
    Object.defineProperty(P, "extend", {value: extend});

    var cached = {};
    function getModule(aName) {
        if (!cached[aName]) {
            var c = global[aName];
            if (c) {
                cached[aName] = new c();
            } else {
                ScriptedResourceClass.executeScriptResource(aName);
                c = global[aName];
                if (c) {
                    cached[aName] = new c();
                } else {
                    throw 'No function: ' + aName + ' found while Modules.get(...).';
                }
            }
        }
        return cached[aName];
    }
    ;
    ScriptUtilsClass.setGetModuleFunc(getModule);
    var PModules = {};
    Object.defineProperty(P, "Modules", {
        value: PModules
    });
    Object.defineProperty(PModules, "get", {
        value: getModule
    });

    function BoundArray() {
        BoundArray.superclass.constructor.apply(this, arguments);
        var target = this;
        var rowset = this.unwrap().getRowset();
        var adapter = new RowsetJSAdapterClass();
        rowset.addRowsetListener(adapter);
        adapter.rowsetFiltered = function() {
            Array.prototype.splice.call(target, 0, target.length);
            var rows = rowset.current;
            var publishedRows = [];
            rows.forEach(function(aRow) {
                publishedRows.push(aRow.getPublished());
            });
            Array.prototype.push.apply(target, publishedRows);
        };
        adapter.rowsetRequeried = function(event) {
            adapter.rowsetFiltered();
        };
        adapter.rowsetNextPageFetched = function(event) {
            adapter.rowsetFiltered();
        };
        adapter.rowsetSaved = function(event) {
            // ignore
        };
        adapter.rowsetRolledback = function(event) {
            // ignore
        };
        adapter.rowsetScrolled = function(event) {
            // ignore
        };
        adapter.rowsetSorted = function(event) {
            adapter.rowsetFiltered();
        };
        adapter.rowInserted = function(event) {
            if (!event.ajusting)
                adapter.rowsetFiltered();
        };
        adapter.rowChanged = function(event) {
            if (event.oldRowCount != event.newRowCount) {
                adapter.rowsetFiltered();
            }
        };
        adapter.rowDeleted = function(event) {
            if (!event.ajusting)
                adapter.rowsetFiltered();
        };

        Object.defineProperty(target, "fill", {
            value: function() {
                throw '\'fill\' is unsupported in BoundArray because of it\'s distinct values requirement';
            }
        });
        Object.defineProperty(target, "pop", {
            value: function() {
                if (!rowset.empty) {
                    var res = rowset.getRow(rowset.size());
                    rowset.deleteAt(rowset.size() - 1, true);
                    Array.prototype.pop.call(target);
                    return res.getPublished();
                }
            }
        });
        Object.defineProperty(target, "push", {
            value: function() {
                if (arguments.length > 1) {
                    for (var a = 0; a < arguments.length; a++) {
                        if (arguments[a].unwrap)
                            rowset.insertAt(rowset.size(), arguments[a].unwrap(), a < arguments.length - 1);
                    }
                } else if (arguments.length === 1) {
                    if (arguments[0].unwrap) {
                        rowset.insertAt(rowset.size(), arguments[0].unwrap(), true);
                        Array.prototype.push.call(target, arguments[0]);
                    }
                }
                return target.length;
            }
        });
        Object.defineProperty(target, "reverse", {
            value: function() {
                rowset.reverse();
            }
        });
        Object.defineProperty(target, "shift", {
            value: function() {
                if (!rowset.empty) {
                    var res = rowset.getRow(1);
                    rowset.deleteAt(0);
                    Array.prototype.shift.call(target);
                    return res.getPublished();
                }
            }
        });
        var defaultCompareFunction = function(o1, o2) {
            var s1 = (o1 + '');
            var s2 = (o2 + '');
            return s1 > s2 ? 1 : s1 < s2 ? -1 : 0;
        };
        Object.defineProperty(target, "sort", {
            value: function() {
                if (arguments.length > 0 && arguments[0] instanceof RowsComparatorClass) {
                    rowset.sort(arguments[0]);
                } else {
                    var compareFunc = defaultCompareFunction;
                    if (arguments.length > 0 && typeof arguments[0] === 'function') {
                        compareFunc = arguments[0];
                    }
                    Array.prototype.sort.call(target, compareFunc);
                }
                return target;
            }
        });
        Object.defineProperty(target, "splice", {
            value: function() {
                if (arguments.length > 0) {
                    var beginToDeleteAt = arguments[0];
                    var howManyToDelete = Number.MAX_VALUE;
                    if (arguments.length > 1) {
                        howManyToDelete = arguments[1];
                    }
                    var needToAdd = arguments.length > 2;
                    var deleted = [];
                    while (!rowset.empty && deleted.length < howManyToDelete) {
                        var rowToDelete = rowset.getRow(beginToDeleteAt + 1);
                        deleted.push(rowToDelete);
                        rowset.deleteAt(beginToDeleteAt, needToAdd);
                    }
                    var insertAt = beginToDeleteAt;
                    for (var a = 2; a < arguments.length; a++) {
                        if (arguments[a].unwrap) {
                            rowset.insertAt(insertAt, arguments[a].unwrap(), a < arguments.length - 1);
                            insertAt++;
                        }
                    }
                    return deleted;
                } else {
                    return [];
                }
            }
        });
        Object.defineProperty(target, "unshift", {
            value: function() {
                if (arguments.length > 1) {
                    for (var a = 0; a < arguments.length; a++) {
                        if (arguments[a].unwrap) {
                            rowset.insertAt(a, arguments[a].unwrap(), a < arguments.length - 1);
                        }
                    }
                } else if (arguments.length === 1) {
                    if (arguments[0].unwrap) {
                        rowset.insertAt(0, arguments[0].unwrap(), true);
                        Array.prototype.unshift.call(target, arguments[0]);
                    }
                }
                return target.length;
            }
        });
    }

    RowClass.setPublisher(function(aDelegate) {
        var nnFields = aDelegate.getFields();
        var instanceCTor = nnFields.getInstanceConstructor();
        var target = !!instanceCTor ? new instanceCTor() : {};
        var nFields = nnFields.toCollection();
        // plain mutable properties
        for (var n = 0; n < nFields.length; n++) {
            (function() {
                var colIndex = n + 1;
                var nField = nFields[n];
                var valueAccessorDesc = {
                    get: function() {
                        return boxAsJs(aDelegate.getColumnObject(colIndex));
                    },
                    set: function(aValue) {
                        aDelegate.setColumnObject(colIndex, boxAsJava(aValue));
                    }
                };
                Object.defineProperty(target, nField.name, valueAccessorDesc);
                Object.defineProperty(target, n, valueAccessorDesc);
            })();
            if (!target.schema)
                Object.defineProperty(target, "schema", {value: nFields.getPublished()});
        }
        // ORM mutable scalar and readonly collection properties
        var ormDefs = nnFields.getOrmDefinitions();
        for (var o in ormDefs.keySet()) {
            var def = ormDefs.get(o);
            Object.defineProperty(target, o, def);
        }
        aDelegate.setPublished(target);
        // WARNING!!! Don't define target.length, because of possible conflict with subject area data properties.
    });
    FieldsClass.setPublisher(function(aDelegate) {
        var target = {};
        var nFields = aDelegate.toCollection();
        for (var n = 0; n < nFields.length; n++) {
            (function() {
                var nField = nFields[n];
                var pField = nField.getPublished();
                Object.defineProperty(target, nField.name, {
                    value: pField
                });
                Object.defineProperty(target, n, {
                    value: pField
                });
            })();
        }
        Object.defineProperty(target, "length", {
            value: nFields.length
        });
        aDelegate.setPublished(target);
    });

    extend(BoundArray, Array);
    extend(P.ApplicationDbEntity, BoundArray);
    extend(P.ApplicationPlatypusEntity, BoundArray);
    extend(P.ApplicationDbParametersEntity, BoundArray);
    extend(P.ApplicationPlatypusParametersEntity, BoundArray);

    /**
     * @static
     * @param {type} aName
     * @param {type} aTarget
     * @returns {P.loadModel.publishTo}
     */
    function loadModel(aName, aTarget) {
        var model = ModelLoaderClass.load(ScriptedResourceClass.getClient(), aName);
        var modelCTor;
        if (model instanceof TwoTierModelClass) {
            modelCTor = P.ApplicationDbModel;
        } else if (model instanceof ThreeTierModelClass) {
            modelCTor = P.ApplicationPlatypusModel;
        } else {
            throw "Can't determine model's type.";
        }
        if (aTarget) {
            modelCTor.call(aTarget, model);
        } else {
            aTarget = new modelCTor(model);
        }
        function publishEntity(aEntity, aName) {
            var published = EngineUtilsClass.unwrap(aEntity.getPublished());
            var pSchema = {};
            Object.defineProperty(published, "schema", {
                value: pSchema
            });
            var nFields = aEntity.getFields().toCollection();
            for (var n = 0; n < nFields.length; n++) {
                (function() {
                    var nField = nFields[n];
                    // params shortcuts
                    if (aEntity instanceof P.ApplicationDbParametersEntity
                            || aEntity instanceof P.ApplicationPlatypusParametersEntity) {
                        var valueDesc = {
                            get: function() {
                                return boxAsJs(nField.value);
                            },
                            set: function(aValue) {
                                nField.value = boxAsJava(aValue);
                            }
                        };
                        Object.defineProperty(published, nField.name, valueDesc);
                        Object.defineProperty(published, n, valueDesc);
                    }
                    // schema
                    var schemaDesc = {
                        value: nField.getPublished()
                    };
                    Object.defineProperty(pSchema, nField.name, schemaDesc);
                    Object.defineProperty(pSchema, n, schemaDesc);
                })();
            }
            // params shortcuts
            if (aEntity instanceof P.ApplicationDbParametersEntity
                    || aEntity instanceof P.ApplicationPlatypusParametersEntity) {
                Object.defineProperty(published, "length", {
                    get: function() {
                        return nFields.length;
                    }
                });
            }
            Object.defineProperty(pSchema, "length", {
                get: function() {
                    return nFields.length;
                }
            });
            Object.defineProperty(aTarget, aName, {
                value: published
            });
        }
        var pEntity = model.getParametersEntity();
        publishEntity(pEntity, "params");
        var entities = model.entities();
        for (var e in entities) {
            var entity = entities[e];
            if (entity.name) {
                publishEntity(entity, entity.name);
            }
        }
        model.createORMDefinitions();
        return aTarget;
    }
    ;
    Object.defineProperty(P, "loadModel", {value: loadModel});
    /**
     * @static
     * @param {type} aName
     * @param {type} aModel
     * @param {type} aTarget
     * @returns {P.loadForm.publishTo}
     */
    function loadForm(aName, aModel, aTarget) {
        var designInfo = FormLoaderClass.load(ScriptedResourceClass.getClient(), aName);
        var form = new FormClass(aName, designInfo, aModel.unwrap());
        if (aTarget) {
            P.Form.call(aTarget, form);
        } else {
            aTarget = new P.Form(form);
        }
        form.injectPublished(aTarget);
        if (!form.title)
            form.title = aName;
        var comps = form.publishedComponents;
        for (var c = 0; c < comps.length; c++) {
            (function() {
                var comp = comps[c];
                if (comp.name) {
                    Object.defineProperty(aTarget, comp.name, {
                        get: function() {
                            return comp;
                        }
                    });
                }
            })();
        }
        return aTarget;
    }
    ;
    Object.defineProperty(P, "loadForm", {value: loadForm});
    /**
     * @static
     * @param {type} aName
     * @param {type} aModel
     * @param {type} aTarget
     * @returns {P.loadTemplate.publishTo}
     */
    function loadTemplate(aName, aModel, aTarget) {
        var publishTo = aTarget ? aTarget : {};
        var template = TemplateLoaderClass.load(ScriptedResourceClass.getClient(), aName, aModel.unwrap());
        // publish
        publishTo.generateReport = function() {
            return template.generateReport();
        };
        return publishTo;
    }
    ;
    Object.defineProperty(P, "loadTemplate", {value: loadTemplate});
    /**
     * Constructs server module network proxy.
     * @constructor
     * @param {String} aModuleName Name of server module (session stateless or statefull or rezident).
     */
    function ServerModule(aModuleName) {
        var client = ScriptedResourceClass.getPlatypusClient();
        if (client) {
            var request = new CreateRequestClass(IDGeneratorClass.genID(), aModuleName);
            client.executeRequest(request);
            var responce = request.getResponse();
            if (responce.isPermitted()) {
                var functions = responce.getFunctionsNames();
                var currentObject = this;
                functions.forEach(function(aFunctionName) {
                    currentObject[aFunctionName] = function() {
                        var onSuccess = null;
                        var onFailure = null;
                        var argsLength = arguments.length;
                        if (arguments.length > 1 && typeof arguments[arguments.length - 1] === "function" && typeof arguments[arguments.length - 2] === "function") {
                            onSuccess = arguments[arguments.length - 2];
                            onFailure = arguments[arguments.length - 1];
                            argsLength -= 2;
                        } else if (arguments.length > 0 && typeof arguments[arguments.length - 1] === "function") {
                            onSuccess = arguments[arguments.length - 1];
                            argsLength -= 1;
                        }
                        try {
                            var params = new JavaArrayClass(argsLength);
                            for (var j = 0; j < argsLength; j++) {
                                params[j] = arguments[j];
                            }
                            var result = client.executeServerModuleMethod(aModuleName, aFunctionName, params);
                            if (onSuccess) {
                                onSuccess(result && result.getPublished ? result.getPublished() : result);
                            } else {
                                return result && result.getPublished ? result.getPublished() : result;
                            }
                        } catch (e) {
                            if (onFailure) {
                                onFailure(e);
                            } else {
                                throw e;
                            }
                        }
                    };
                });
            } else {
                throw "Access denied for module" + aModuleName + ". May be denied public access."
            }
        } else {
            throw "This architecture does not support server modules."
        }
    }
    ;
    Object.defineProperty(P, "ServerModule", {value: ServerModule});
    var toPrimitive = ScriptUtilsClass.getToPrimitiveFunc();
    /**
     * @private
     * @param {type} aValue
     * @returns {unresolved}
     */
    function boxAsJava(aValue) {
        //---------------------- Don't change to !== because of undefined 
        if (arguments.length > 0 && aValue != null) {
            if (aValue.unwrap) {
                aValue = aValue.unwrap();
            } else {
                aValue = toPrimitive(aValue);
            }
            return aValue;
        } else {// undefined -> null
            return null;
        }
    }
    ;
    Object.defineProperty(P, "boxAsJava", {
        value: boxAsJava
    });
    /**
     * @private
     * @param {type} aValue
     * @returns {unresolved}
     */
    function boxAsJs(aValue) {
        if (aValue) {
            if (aValue.getPublished) {
                if (arguments.length > 1) {
                    var elementClass = arguments[1];
                    aValue = aValue.getPublished(new elementClass());
                } else {
                    aValue = aValue.getPublished();
                }
            } else if (aValue instanceof JavaDateClass) {
                aValue = new Date(aValue.time);
            } else if (aValue instanceof JavaArrayClass) {
                var converted = [];
                for (var i = 0; i < aValue.length; i++) {
                    if (arguments.length > 1) {
                        converted[converted.length] = boxAsJs(aValue[i], arguments[1]);
                    } else {
                        converted[converted.length] = boxAsJs(aValue[i]);
                    }
                }
                return converted;
            }
            aValue = EngineUtilsClass.unwrap(aValue);
        }
        return aValue;
    }
    ;
    Object.defineProperty(P, "boxAsJs", {
        value: boxAsJs
    });

    var Resource = {};
    Object.defineProperty(Resource, "load", {
        value: function(aResName, onSuccess, onFailure) {
            try {
                var loaded = ScriptedResourceClass.load(aResName);
            } catch (e) {
                if (onFailure)
                    onFailure(e);
                else
                    throw e;
            }
            if (onSuccess) {
                onSuccess(loaded);
            } else {
                return loaded;
            }
        }
    });
    Object.defineProperty(Resource, "loadText", {
        value: function(aResName, aOnSuccessOrEncoding, aOnSuccessOrOnFailure, aUndefinedOrOnFailure) {
            try {
                var encoding = null;
                var onSuccess = null;
                var onFailure = null;
                if (arguments.length === 1) {
                    // no op. default values
                } else if (arguments.length === 2) {
                    if (typeof aOnSuccessOrEncoding === 'string')
                        encoding = aOnSuccessOrEncoding;
                    else if (typeof aOnSuccessOrEncoding === 'function') {
                        onSuccess = aOnSuccessOrEncoding;
                    }
                } else if (arguments.length === 3) {
                    if (typeof aOnSuccessOrEncoding === 'string') {
                        encoding = aOnSuccessOrEncoding;
                        onSuccess = aOnSuccessOrOnFailure;
                    } else if (typeof aOnSuccessOrEncoding === 'function') {
                        onSuccess = aOnSuccessOrEncoding;
                        onFailure = aOnSuccessOrOnFailure;
                    }
                } else if (arguments.length === 4) {
                    encoding = aOnSuccessOrEncoding;
                    onSuccess = aOnSuccessOrOnFailure;
                    onFailure = aUndefinedOrOnFailure;
                }
                var loaded = encoding ? ScriptedResourceClass.loadText(aResName, encoding) : ScriptedResourceClass.loadText(aResName);
            } catch (e) {
                if (onFailure)
                    onFailure(e);
                else
                    throw e;
            }
            if (onSuccess) {
                onSuccess(loaded);
            } else {
                return loaded;
            }
        }
    });
    Object.defineProperty(Resource, "upload", {
        value: function(aFile, aCompleteCallback, aProgressCallback, aAbortCallback) {
            printf("upload() is not implemented for J2SE");
        }
    });
    Object.defineProperty(Resource, "applicationPath", {
        get: function() {
            return ScriptedResourceClass.getApplicationPath();
        }
    });
    Object.defineProperty(P, "Resource", {
        value: Resource
    });
    Object.defineProperty(P, "logout", {
        value: function(onSuccess, onFailure) {
            printf("logout() is not implemented for J2SE");
        }
    });

    Object.defineProperty(P.Color, "black", {value: new P.Color(0, 0, 0)});
    Object.defineProperty(P.Color, "BLACK", {value: new P.Color(0, 0, 0)});
    Object.defineProperty(P.Color, "blue", {value: new P.Color(0, 0, 0xff)});
    Object.defineProperty(P.Color, "BLUE", {value: new P.Color(0, 0, 0xff)});
    Object.defineProperty(P.Color, "cyan", {value: new P.Color(0, 0xff, 0xff)});
    Object.defineProperty(P.Color, "CYAN", {value: new P.Color(0, 0xff, 0xff)});
    Object.defineProperty(P.Color, "DARK_GRAY", {value: new P.Color(0x40, 0x40, 0x40)});
    Object.defineProperty(P.Color, "darkGray", {value: new P.Color(0x40, 0x40, 0x40)});
    Object.defineProperty(P.Color, "gray", {value: new P.Color(0x80, 0x80, 0x80)});
    Object.defineProperty(P.Color, "GRAY", {value: new P.Color(0x80, 0x80, 0x80)});
    Object.defineProperty(P.Color, "green", {value: new P.Color(0, 0xff, 0)});
    Object.defineProperty(P.Color, "GREEN", {value: new P.Color(0, 0xff, 0)});
    Object.defineProperty(P.Color, "LIGHT_GRAY", {value: new P.Color(0xC0, 0xC0, 0xC0)});
    Object.defineProperty(P.Color, "lightGray", {value: new P.Color(0xC0, 0xC0, 0xC0)});
    Object.defineProperty(P.Color, "magenta", {value: new P.Color(0xff, 0, 0xff)});
    Object.defineProperty(P.Color, "MAGENTA", {value: new P.Color(0xff, 0, 0xff)});
    Object.defineProperty(P.Color, "orange", {value: new P.Color(0xff, 0xC8, 0)});
    Object.defineProperty(P.Color, "ORANGE", {value: new P.Color(0xff, 0xC8, 0)});
    Object.defineProperty(P.Color, "pink", {value: new P.Color(0xFF, 0xAF, 0xAF)});
    Object.defineProperty(P.Color, "PINK", {value: new P.Color(0xFF, 0xAF, 0xAF)});
    Object.defineProperty(P.Color, "red", {value: new P.Color(0xFF, 0, 0)});
    Object.defineProperty(P.Color, "RED", {value: new P.Color(0xFF, 0, 0)});
    Object.defineProperty(P.Color, "white", {value: new P.Color(0xFF, 0xff, 0xff)});
    Object.defineProperty(P.Color, "WHITE", {value: new P.Color(0xFF, 0xff, 0xff)});
    Object.defineProperty(P.Color, "yellow", {value: new P.Color(0xFF, 0xff, 0)});
    Object.defineProperty(P.Color, "YELLOW", {value: new P.Color(0xFF, 0xff, 0)});
    P.Colors = P.Color;

    Object.defineProperty(P.Cursor, "CROSSHAIR", {value: new P.Cursor(1)});
    Object.defineProperty(P.Cursor, "DEFAULT", {value: new P.Cursor(0)});
    Object.defineProperty(P.Cursor, "AUTO", {value: new P.Cursor(0)});
    Object.defineProperty(P.Cursor, "E_RESIZE", {value: new P.Cursor(11)});
// help ?
// progress ?
    Object.defineProperty(P.Cursor, "HAND", {value: new P.Cursor(12)});
    Object.defineProperty(P.Cursor, "MOVE", {value: new P.Cursor(13)});
    Object.defineProperty(P.Cursor, "NE_RESIZE", {value: new P.Cursor(7)});
    Object.defineProperty(P.Cursor, "NW_RESIZE", {value: new P.Cursor(6)});
    Object.defineProperty(P.Cursor, "N_RESIZE", {value: new P.Cursor(8)});
    Object.defineProperty(P.Cursor, "SE_RESIZE", {value: new P.Cursor(5)});
    Object.defineProperty(P.Cursor, "SW_RESIZE", {value: new P.Cursor(4)});
    Object.defineProperty(P.Cursor, "S_RESIZE", {value: new P.Cursor(9)});
    Object.defineProperty(P.Cursor, "TEXT", {value: new P.Cursor(2)});
    Object.defineProperty(P.Cursor, "WAIT", {value: new P.Cursor(3)});
    Object.defineProperty(P.Cursor, "W_RESIZE", {value: new P.Cursor(10)});

    var Icon = {};
    Object.defineProperty(P, "Icon", {value: Icon});
    Object.defineProperty(Icon, "load", {
        value: function(aPath) {
            return IconResourcesClass.load(aPath);
        }
    });
    P.Icons = P.Icon;
    var IDGenerator = {};

    Object.defineProperty(P, "IDGenerator", {value: IDGenerator});
    Object.defineProperty(IDGenerator, "genID", {
        value: function() {
            return IDGeneratorClass.genID();
        }
    });
    Object.defineProperty(P, "ID", {value: IDGenerator});
    Object.defineProperty(IDGenerator, "generate", {
        value: function() {
            return IDGeneratorClass.genID();
        }
    });
    var MD5Generator = {};
    Object.defineProperty(P, "MD5Generator", {value: MD5Generator});
    Object.defineProperty(MD5Generator, "generate", {
        value: function(aSource) {
            return MD5GeneratorClass.generate(aSource).toLowerCase();
        }
    });
    var MD5 = {};
    Object.defineProperty(P, "MD5", {value: MD5});
    Object.defineProperty(MD5, "generate", {
        value: function(aSource) {
            return MD5GeneratorClass.generate(aSource).toLowerCase();
        }
    });

    var applicationLogger = LoggerClass.getLogger("Application");
    var Logger = {};
    Object.defineProperty(P, "Logger", {value: Logger});
    Object.defineProperty(Logger, "config", {value: function(aMessage) {
            applicationLogger.config(aMessage !== null ? "" + aMessage : null);
        }});
    Object.defineProperty(Logger, "severe", {value: function(aMessage) {
            applicationLogger.severe(aMessage !== null ? "" + aMessage : null);
        }});
    Object.defineProperty(Logger, "warning", {value: function(aMessage) {
            applicationLogger.warning(aMessage !== null ? "" + aMessage : null);
        }});
    Object.defineProperty(Logger, "info", {value: function(aMessage) {
            applicationLogger.info(aMessage !== null ? "" + aMessage : null);
        }});
    Object.defineProperty(Logger, "fine", {value: function(aMessage) {
            applicationLogger.fine(aMessage !== null ? "" + aMessage : null);
        }});
    Object.defineProperty(Logger, "finer", {value: function(aMessage) {
            applicationLogger.finer(aMessage !== null ? "" + aMessage : null);
        }});
    Object.defineProperty(Logger, "finest", {value: function(aMessage) {
            applicationLogger.finest(aMessage !== null ? "" + aMessage : null);
        }});

    Object.defineProperty(P, "VK_ALT", {value: KeyEventClass.VK_ALT});
    Object.defineProperty(P, "VK_BACKSPACE", {value: KeyEventClass.VK_BACK_SPACE});
    Object.defineProperty(P, "VK_DELETE", {value: KeyEventClass.VK_DELETE});
    Object.defineProperty(P, "VK_DOWN", {value: KeyEventClass.VK_DOWN});
    Object.defineProperty(P, "VK_END", {value: KeyEventClass.VK_END});
    Object.defineProperty(P, "VK_ENTER", {value: KeyEventClass.VK_ENTER});
    Object.defineProperty(P, "VK_ESCAPE", {value: KeyEventClass.VK_ESCAPE});
    Object.defineProperty(P, "VK_HOME", {value: KeyEventClass.VK_HOME});
    Object.defineProperty(P, "VK_LEFT", {value: KeyEventClass.VK_LEFT});
    Object.defineProperty(P, "VK_PAGEDOWN", {value: KeyEventClass.VK_PAGE_DOWN});
    Object.defineProperty(P, "VK_PAGEUP", {value: KeyEventClass.VK_PAGE_UP});
    Object.defineProperty(P, "VK_RIGHT", {value: KeyEventClass.VK_RIGHT});
    Object.defineProperty(P, "VK_SHIFT", {value: KeyEventClass.VK_SHIFT});
    Object.defineProperty(P, "VK_TAB", {value: KeyEventClass.VK_TAB});
    Object.defineProperty(P, "VK_UP", {value: KeyEventClass.VK_UP});

    /**
     * Opens a file dialog box 
     *
     * @param curDir current directory [optional]
     * @param save flag tells whether this is a save dialog or not
     * @return selected file or else null
     */
    function fileDialog(curDir, save) {
        var result;
        function _fileDialog() {
            if (!curDir) {
                curDir = ".";
            }
            var dialog = new FileChooserClass(new FileClass(curDir));
            var res = save ? dialog.showSaveDialog(null) :
                    dialog.showOpenDialog(null);
            if (res === FileChooserClass.APPROVE_OPTION) {
                result = dialog.getSelectedFile();
            } else {
                result = null;
            }
        }

        _fileDialog();
        return result !== null ? result.getPath() : null;
    }
    fileDialog.docString = "show a file dialog box";

    function selectFile(curDir, aCallback) {
        if (aCallback) {
            (function() {
                var file = fileDialog(curDir, false);
                aCallback(file);
            }).invokeLater();
        } else {
            return fileDialog(curDir, false);
        }
    }
    Object.defineProperty(P, "selectFile", {
        value: selectFile
    });

    /**
     * Opens a directory dialog box 
     *
     * @param curDir current directory [optional]
     * @return selected directory or else null
     */
    function directoryDialog(curDir) {
        var result;
        function _dirDialog() {
            if (curDir === undefined) {
                curDir = new FileClass(".");
            }
            var dialog = new FileChooserClass(curDir);
            dialog.setFileSelectionMode(FileChooserClass.DIRECTORIES_ONLY);
            dialog.setAcceptAllFileFilterUsed(false);
            var res = dialog.showOpenDialog(null);
            if (res === FileChooserClass.APPROVE_OPTION) {
                result = dialog.getSelectedFile();
            } else {
                result = null;
            }
        }
        _dirDialog();
        return result;
    }
    directoryDialog.docString = "shows a directory dialog box";
    function selectDirectory(curDir, aCallback) {
        if (aCallback) {
            (function() {
                var file = directoryDialog(curDir);
                aCallback(file);
            }).invokeLater();
        } else {
            return directoryDialog(curDir);
        }
    }

    Object.defineProperty(P, "selectDirectory", {
        value: selectDirectory
    });
    /**
     * Opens a color chooser dialog box 
     *
     * @param title of the dialog box [optional]
     * @param color default color [optional]
     * @return choosen color or default color
     */
    function colorDialog(title, color) {
        var result;
        function _colorDialog() {
            if (!title) {
                title = "Choose Color";
            }
            if (!color) {
                color = P.Color.BLACK;
            }
            var res = ColorChooserClass.showDialog(null, title, color ? color.unwrap() : null);
            result = res ? (new ColorClass(res)).getPublished() : null;
        }
        _colorDialog();
        return result;
    }
    colorDialog.docString = "shows a color chooser dialog box";
    function selectColor(title, color, aCallback) {
        if (aCallback) {
            (function() {
                var selected = colorDialog(title, color);
                aCallback(selected);
            }).invokeLater();
        } else {
            return colorDialog(title, color);
        }
    }

    Object.defineProperty(P, "selectColor", {
        value: selectColor
    });

    function readString(aFileName, aEncoding) {
        var encoding = 'utf-8';
        if (aEncoding) {
            encoding = aEncoding;
        }
        return FileUtilsClass.readString(new FileClass(aFileName), encoding);
    }

    Object.defineProperty(P, "readString", {
        value: readString
    });
    function writeString(aFileName, aText, aEncoding) {
        var encoding = 'utf-8';
        if (aEncoding) {
            encoding = aEncoding;
        }
        FileUtilsClass.writeString(new FileClass(aFileName), aText, encoding);
    }
    Object.defineProperty(P, "writeString", {
        value: writeString
    });

    Object.defineProperty(P.Form, "shown", {
        get: function() {
            var nativeArray = FormClass.getShownForms();
            var res = [];
            for (var i = 0; i < nativeArray.length; i++)
                res[res.length] = nativeArray[i].getPublished();
            return res;
        }
    });

    Object.defineProperty(P.Form, "getShownForm", {
        value: function(aName) {
            var shownForm = FormClass.getShownForm(aName);
            return shownForm !== null ? shownForm.getPublished() : null;
        }
    });

    Object.defineProperty(P.Form, "onChange", {
        get: function() {
            return FormClass.getOnChange();
        },
        set: function(aValue) {
            FormClass.setOnChange(aValue);
        }
    });

    /**
     * Shows a message box
     *
     * @param msg message to be shown
     * @param title title of message box [optional]
     * @param msgType type of message box [constants in JOptionPane]
     */
    function msgBox(msg, title, msgType) {

        function _msgBox() {
            if (msg === undefined)
                msg = "undefined";
            if (msg === null)
                msg = "null";
            if (!title)
                title = msg;
            if (!msgType)
                msgType = OptionPaneClass.INFORMATION_MESSAGE;
            OptionPaneClass.showMessageDialog(null, msg, title, msgType);
        }
        _msgBox();
    }
    msgBox.docString = "shows MessageBox to the user";
    Object.defineProperty(P, "msgBox", {
        value: msgBox
    });

    /**
     * Shows an information alert box
     *
     * @param msg message to be shown
     * @param title title of message box [optional]
     */
    function alert(msg, title) {
        msgBox(msg, title, OptionPaneClass.INFORMATION_MESSAGE);
    }
    alert.docString = "shows an alert message box to the user";
    Object.defineProperty(global, "alert", {
        value: alert
    });

    /**
     * Shows an error alert box
     *
     * @param msg message to be shown
     * @param title title of message box [optional]
     */
    function error(msg, title) {
        msgBox(msg, title, OptionPaneClass.ERROR_MESSAGE);
    }
    error.docString = "shows an error message box to the user";
    Object.defineProperty(P, "error", {
        value: error
    });

    /**
     * Shows a warning alert box
     *
     * @param msg message to be shown
     * @param title title of message box [optional]
     */
    function warn(msg, title) {
        msgBox(msg, title, OptionPaneClass.WARNING_MESSAGE);
    }
    warn.docString = "shows a warning message box to the user";
    Object.defineProperty(P, "warn", {
        value: warn
    });

    /**
     * Shows a prompt dialog box
     *
     * @param question question to be asked
     * @param answer default answer suggested [optional]
     * @param title title of message box [optional]
     * @return answer given by user
     */
    function prompt(question, answer, title) {
        var result;
        function _prompt() {
            if (answer === undefined)
                answer = "";
            if (title === undefined)
                title = "Input";
            result = OptionPaneClass.showInputDialog(null, question, title, OptionPaneClass.QUESTION_MESSAGE, null, null, answer);
            //result = OptionPaneClass.showInputDialog(null, question, answer);
        }
        _prompt();
        return result;
    }
    prompt.docString = "shows a prompt box to the user and returns the answer";
    Object.defineProperty(global, "prompt", {
        value: prompt
    });

    /**
     * Shows a confirmation dialog box
     *
     * @param msg message to be shown
     * @param title title of message box [optional]
     * @return boolean (yes->true, no->false)
     */
    function confirm(msg, title) {
        var result;
        function _confirm() {
            if (title === undefined)
                title = msg;
            var optionType = OptionPaneClass.YES_NO_OPTION;
            result = OptionPaneClass.showConfirmDialog(null, msg, title, optionType);
        }
        _confirm();
        return result === OptionPaneClass.YES_OPTION;
    }
    confirm.docString = "shows a confirmation message box to the user";
    Object.defineProperty(global, "confirm", {
        value: confirm
    });

    var HorizontalPosition = {};
    Object.defineProperty(HorizontalPosition, "LEFT", {
        value: HorizontalPositionClass.LEFT
    });
    Object.defineProperty(HorizontalPosition, "CENTER", {
        value: HorizontalPositionClass.CENTER
    });
    Object.defineProperty(HorizontalPosition, "RIGHT", {
        value: HorizontalPositionClass.RIGHT
    });
    Object.defineProperty(P, "HorizontalPosition", {
        value: HorizontalPosition
    });
//
    var VerticalPosition = {};
    Object.defineProperty(VerticalPosition, "TOP", {
        value: VerticalPositionClass.TOP
    });
    Object.defineProperty(VerticalPosition, "CENTER", {
        value: VerticalPositionClass.CENTER
    });
    Object.defineProperty(VerticalPosition, "BOTTOM", {
        value: VerticalPositionClass.BOTTOM
    });
    Object.defineProperty(P, "VerticalPosition", {
        value: VerticalPosition
    });
//
    var Orientation = {};
    Object.defineProperty(Orientation, "HORIZONTAL", {
        value: OrientationClass.HORIZONTAL
    });
    Object.defineProperty(Orientation, "VERTICAL", {
        value: OrientationClass.VERTICAL
    });
    Object.defineProperty(P, "Orientation", {
        value: Orientation
    });
    //
    var FontStyleClass = Java.type("com.eas.gui.FontStyle");
    var FontStyle = {};
    Object.defineProperty(FontStyle, "ITALIC", {
        value: FontStyleClass.ITALIC
    });
    Object.defineProperty(FontStyle, "BOLD", {
        value: FontStyleClass.BOLD
    });
    Object.defineProperty(FontStyle, "BOLD_ITALIC", {
        value: FontStyleClass.BOLD_ITALIC
    });
    Object.defineProperty(FontStyle, "NORMAL", {
        value: FontStyleClass.NORMAL
    });
    Object.defineProperty(P, "FontStyle", {
        value: FontStyle
    }); 
})();
if (!P) {
    /** 
     * Platypus library namespace global variable.
     * @namespace P
     */
    var P;
    P.HTML5 = "";
    P.J2SE = "";
    P.agent = "";
    P.require = function() {
    };
    P.extend = function() {
    };
    P.Modules;
    P.loadModel = function() {
    };
    P.loadForm = function() {
    };
    P.loadTemplate = function() {
    };
    /**
     * Constructs server module network proxy.
     * @constructor
     */
    P.ServerModule = function() {
    };
    P.boxAsJava = function() {
    };
    P.boxAsJs = function() {
    };
    P.Resource = {};
    P.logout = function() {
    };
    P.Icon = {};
    P.ID = {generate: function(aValue) {
            return "";
        }};
    
    /**
     * Md5 hash generator
     * @type type
     */
    P.MD5 = {
        /**
         * Generates MD5 hash for given value. 
         * @param aValue Value the hash is generated for. Converted to string.
         * @return Generated MD5 hash
         */
        generate: function(aValue) {
            return "";
        }
    };
    P.Logger = {};
    P.VK_ALT = 0;
    P.VK_BACKSPACE = 0;
    P.VK_DELETE = 0;
    P.VK_DOWN = 0;
    P.VK_END = 0;
    P.VK_ENTER = 0;
    P.VK_ESCAPE = 0;
    P.VK_HOME = 0;
    P.VK_LEFT = 0;
    P.VK_PAGEDOWN = 0;
    P.VK_PAGEUP = 0;
    P.VK_RIGHT = 0;
    P.VK_SHIFT = 0;
    P.VK_TAB = 0;
    P.VK_UP = 0;
    P.selectFile = function() {
    };
    P.selectDirectory = function() {
    };
    P.selectColor = function() {
    };
    P.readString = function() {
    };
    P.writeString = function() {
    };
    P.msgBox = function() {
    };
    P.warn = function() {
    };
    P.prompt = function() {
    };
    P.HorizontalPosition = {};
    P.VerticalPosition = {};
    P.Orientation = {};
    P.FontStyle = {};
}
