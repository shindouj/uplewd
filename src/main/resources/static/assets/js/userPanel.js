const refreshTokenButton = document.getElementById("refreshTokenButton");
const tokenParagraph = document.getElementById("userTokenParagraph");

const copyTokenButton = document.getElementById("copyTokenButton");

refreshTokenButton.addEventListener("click", function (event) {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    let url = window.location.origin + "/userPanel/refreshToken";

    let request = new XMLHttpRequest();
    request.onreadystatechange = function () {
        if (request.readyState === XMLHttpRequest.DONE) {
            let response = JSON.parse(request.responseText);
            tokenParagraph.innerText = response.token;
        }
    };

    request.addEventListener("error", function (ev) {
        alert(`error: ${request.responseText}`);
    });

    request.open("PATCH", url);
    request.setRequestHeader(header, token);
    request.send();
});

copyTokenButton.addEventListener("click", function (event) {
    navigator.clipboard.writeText(tokenParagraph.innerText);
})