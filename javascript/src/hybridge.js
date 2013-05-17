(function() {
  var Hybridge = new function() {
    var HYBRIDGE_VERSION = "1.0";
    var HYBRIDGE_ID_PREFIX = "bridge.";

    var self = function Hybridge() {
      self.ids = {};
      self.idsCount = 0;
      self.callbackTable = {};
      self.observerTable = {};
    };

    self.prototype = {
      constructor: self,
      call: function(dest, params, callback) {
        if (typeof(params) == 'function') {
          callback = params;
          params = {};
        }

        var request = {
          'id': this.getId(),
          'dest': dest,
          'params': params
        };

        this.notifyToDevice(request, callback);
      },
      getId: function() {
        var id = HYBRIDGE_ID_PREFIX + self.idsCount;
        self.ids[id] = true;
        self.idsCount++;
        return id;
      },
      notifyToDevice: function(request, callback) {
        var requestJson = JSON.stringify(request);
        if (callback && request.id) {
          self.callbackTable[request.id] = callback;
        }

        if (this.existsDeviceBridge()) {
          window.DeviceBridge.notifyToDevice(encodeURIComponent(requestJson));
        } else {
          // TODO stack request from web
        }
      },
      existsDeviceBridge: function() {
        return !!(window.DeviceBridge);
      },
      notifyToWeb: function() {
        var json = {};
        try {
          json = JSON.parse(decodeURIComponent(notify));
        } catch (e) {
          // TODO notify error to device
        }

        if (this.isRequestNotify(json)) {
          var observers = self.observerTable[json.dest];
          if (observers) {
            for (var i = 0; i < observers.length; i++) {
              var observer = observers[i];
              this.observer(json);
            }
          }
        } else {
          var callback = self.callbackTable[json.id];
          if (callback) callback(json);
          delete self.callbackTable[json.id];
        }
      },
      isRequestNotify: function(notify) {
        return (notify && notify.hasOwnProperty('params')) ? true : false;
      },
      observe: function(dest, observer) {
        if (!dest && !observer) return;

        if (!self.observerTable[dest]) {
          self.observerTable[dest] = [];
        }
        self.observer[dest].push(observer);
      }
    };

    return self;
  };

  window.hybridge = new Hybridge();
}).call(this);
