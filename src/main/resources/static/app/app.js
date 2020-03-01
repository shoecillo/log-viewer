
var arkApp = angular.module('logviewerApp', ["ngRoute"]);

arkApp.config(['$routeProvider',"$locationProvider",
  function($routeProvider,$locationProvider) {
	//$locationProvider.html5Mode(true);
    $routeProvider.
      when('/:app', {
        templateUrl: 'partials/list-logs.html',
        controller: 'logListCtrl'
      }).
      otherwise({
        redirectTo: '/'
      });
  }]);




arkApp.controller("logViewerCtrl",["$scope","$http","$interval","$sce",function($scope,$http,$interval,$sce){
	
	var vm = $scope;
	
	vm.appz = [];
	vm.lsLogs = [];
	vm.fileContent = "";
	vm.selectedApp = "";
	
	vm.getAppz = function()
	{
		$http.get('/getAppz').then(function(result)
		{
			vm.appz = result.data;
			
		}, 
		function(error)
		{
			console.log("ERROR");
		
		});
	};
	
	$scope.$on('list-log-evt',function(evt,param){
		vm.selectedApp = param;
	});
	
	
	vm.getAppz();
	
}]);

arkApp.controller("logListCtrl",["$scope","$rootScope","$http","$interval","$sce","$routeParams",function($scope,$rootScope,$http,$interval,$sce,$routeParams){
	
	var vm = $scope;
	
	
	vm.lsLogs = [];
	vm.fileContent = "";
	vm.selectedApp = "";
	
	vm.getLogs = function($routeParams)
	{
		$http.get('/getLogs/'+$routeParams.app).then(function(result)
		{
			vm.lsLogs = result.data;
			vm.selectedApp = $routeParams.app;
			
		}, 
		function(error)
		{
			console.log(error);
			if(error.status == 400){
				window.location.href="/";
			}
		
		});
	};
	
	vm.getFile = function(filename)
	{
		$http.post('/getFileContent/'+vm.selectedApp+'/'+filename).then(function(result)
		{
			vm.fileContent = $sce.trustAsHtml(result.data);
			var win = window.open("","_blank");
			win.document.body.style.whiteSpace = "nowrap";
			win.document.body.style.width = "100%";
			win.document.title = filename;
			win.document.body.innerHTML = vm.fileContent;
			
		}, 
		function(error)
		{
			console.log("ERROR");
		
		});
	};
	
	vm.getLogs($routeParams);
	$rootScope.$broadcast('list-log-evt',$routeParams.app);
	
}]);