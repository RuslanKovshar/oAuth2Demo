let app = angular.module("mainApp", []);

app.controller("AppCtrl", function ($scope, $http) {
    $scope.user = {};
    $scope.userPic = {};
    $http({
        method: "GET",
        url: "/api/user",
        headers: {"Content-Type": "application/json"}
    }).then(function (data) {
        let user = data.data.principal;
        console.log(user);
        $scope.user = user.login;
        $scope.userPic = user.avatarPath;
    });

});
