<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
    <script type="text/javascript" src="${pageContext.request.contextPath}/dojo/dojo.js.uncompressed.js"></script>
    <script type="text/javascript" src="application.js"></script>
    <%--
    The reason to use a JSP is that it is very easy to obtain server-side configuration
    information (such as the contextPath) and pass it to the JavaScript environment on the client.
    --%>
    <script type="text/javascript">
        var config = {
            contextPath: '${pageContext.request.contextPath}'
        };
    </script>
            <script type="text/javascript">
            dojo.require("dojox.cometd");
            dojo.require("dojox.cometd.timestamp");
            $ = dojo.byId;

            var echoBehaviours = { 
                '#sendB': {
                    "onclick": function(e){
            			_sendEcho();
                        $('phrase').value='';
                        return false;
                    }
                }
            };
        </script>
</head>
<body>
 	   <div id="body"></div>


        <h1>Echo test</h1>
		<a href="../..">Main Demo index</a><br/>
        <p>
            Echo data to ONLY this client using RPC style messaging over
            cometd. Requires a server side component at /service/echo which echos
            responses directly to the client.
        </p>
        <div>
            Echo: <input id="phrase" type="text"></input> <input id="sendB" class="button" type="submit" name="join" value="Send"/>
        </div>
        <pre id="responses"></pre>

</body>
</html>
