function OnClickLoginButtonEvent(e)
{
    var username = document.getElementById("username_textbox").value;
    var password = document.getElementById("password_textbox").value;

    var http = new XMLHttpRequest();
    var url = '/Login';
    var params = '?username='+username+'&password='+password;
    http.open('POST', url, true);

    //Send the proper header information along with the request
    http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    http.setRequestHeader('TTS', '$%12558585^%%vdfvdGGQ52cdsc8585RFVFVV');

    http.onreadystatechange = function()
    {
        if(http.readyState == 4 && http.status == 200)
        {

            var res = JSON.parse(http.responseText);
            if(!res.message.includes("not"))
            {
                // localStorage.setItem("auth",res.token);
                setCookie("auth",res.token,1);
                window.location.href="/Dashboard";
            }
            else
            {
                alert("User not found")
            }

        }
    }

    http.send(params);
}

function OnClickLogotButton(e)
{
    setCookie("auth","",1);
    window.location.href="/";
}

function setCookie(name,value,days)
{
    var expires = "";
    if (days) {
        var date = new Date();
        date.setTime(date.getTime() + (days*24*60*60*1000));
        expires = "; expires=" + date.toUTCString();
    }
    document.cookie = name + "=" + (value || "")  + expires + "; path=/";
}

function getCookie(name)
{
    var nameEQ = name + "=";
    var ca = document.cookie.split(';');
    for(var i=0;i < ca.length;i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1,c.length);
        if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length,c.length);
    }
    return null;
}

function eraseCookie(name)
{
    document.cookie = name +'=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}

function removeAuthCookie(cookie_name)
{
    // Set expiration date to the past by using expires or setting max-age to 0
    document.cookie = cookie_name+"=; path=/; max-age=0;";
    // Alternative method using expires
    // document.cookie = "auth=; path=/; expires=Thu, 01 Jan 1970 00:00:00 UTC;";

    console.log("Auth cookie has been removed");
}


function getCookie(name) {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
}



function RemoveUserButtonClickEvent(user_id)
{
    if(confirm("Are You Sure?")) {
        // Create the URL with query parameter
        const url = `/Panel/RemoveUser?user_id=${encodeURIComponent(user_id)}`;

        fetch(url, {
            method: 'POST',
            credentials: 'include', // Ensures cookies like sessionid are sent
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Cookie': 'auth=' + getCookie("auth")
            }
        })
            .then(response => response.text())
            .then(data => {
                console.log('Response:', data);
            })
            .catch(error => {
                console.error('Error:', error);
            });

        location.reload(true);
    }
}



function GetChangeConnectionStatusOnClickEvent(c_id,status)
{
    if(confirm("Are You Sure?")) {
        // Create the URL with query parameter
        const url = `/Panel/ChangeStatusConnection?c_id=${encodeURIComponent(c_id)}&status=${encodeURIComponent(status)}`;

        fetch(url, {
            method: 'POST',
            credentials: 'include', // Ensures cookies like sessionid are sent
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Cookie': 'auth=' + getCookie("auth")
            }
        })
            .then(response => response.text())
            .then(data => {
                console.log('Response:', data);
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }
    location.reload(true);
}



function GetRemoveConnectionOnClickEvent(c_id)
{
    if(confirm("Are You Sure?")) {
        // Create the URL with query parameter
        const url = `/Panel/RemoveConnection?c_id=${encodeURIComponent(c_id)}`;

        fetch(url, {
            method: 'POST',
            credentials: 'include', // Ensures cookies like sessionid are sent
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Cookie': 'auth=' + getCookie("auth")
            }
        })
            .then(response => response.text())
            .then(data => {
                console.log('Response:', data);
            })
            .catch(error => {
                console.error('Error:', error);
            });

        location.reload(true);
    }
}


function copyToClipboard(text) {
    if (!navigator.clipboard) {
        // Fallback for older browsers
        const textarea = document.createElement("textarea");
        textarea.value = text;
        textarea.style.position = "fixed"; // avoid scrolling to bottom
        document.body.appendChild(textarea);
        textarea.focus();
        textarea.select();
        try {
            document.execCommand("copy");
            console.log("Text copied using fallback");
        } catch (err) {
            console.error("Fallback copy failed", err);
        }
        document.body.removeChild(textarea);
    } else {
        navigator.clipboard.writeText(text)
            .then(() => {
                console.log("Text copied to clipboard");
            })
            .catch(err => {
                console.error("Failed to copy text", err);
            });
    }
    alert("Connection Key Copied To Clipboard");
}
