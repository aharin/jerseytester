<html>
<head>
    <title>default</title>
</head>
<body><span id="click-me">click me</span></body>
<script>document.body.children[0].addEventListener('click', function (){ document.title = "worked";});</script>
</html>