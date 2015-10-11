<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html
        PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title>Status</title>
</head>
<body>

<h1>Status Page</h1>
<dl>
    <dt>Accept:</dt>
    <dd id="accept_header.value">${accept!}</dd>
</dl>
<dl>
    <dt>Cookie:</dt>
    <dd id="cookie_header.value">${cookie!}</dd>
</dl>
<form id="my-form" onsubmit="return triggerAjaxPost()" method="post">
    <input name="${dummy_key}" value=${dummy_value}/>
    <input id="ajax-post" type="submit" value="try an ajax post"/>
</form>
<span id="form-span"></span>
<span id="my-span" onclick="triggerAjaxGet()">Lol</span>
<script>
    function triggerAjaxPost() {
        var form = document.forms[0];

        form.onsubmit = function (e) {
            e.preventDefault();
            var data = {};
            for (var i = 0, ii = form.length; i < ii; ++i) {
                var input = form[i];
                if (input.name) {
                    data[input.name] = input.value;
                }
            }

            var xhr = new XMLHttpRequest();
            xhr.open(form.method, form.action, true);
            xhr.setRequestHeader('Content-Type', 'application/json; charset=UTF-8');

            xhr.send(JSON.stringify(data));

            xhr.onloadend = function () {
            };
        };
    }

    function triggerAjaxGet() {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.status === 204) {
                document.getElementById("my-span").innerHTML = "there was no content";
            }
        };
        xhr.open("GET", "/no-content", true);
        xhr.send();
    }
</script>
</body>
</html>
