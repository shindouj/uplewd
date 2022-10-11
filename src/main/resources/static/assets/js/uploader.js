if (!String.prototype.format) {
    String.prototype.format = function (...args) {
        return this.replace(/(\{\d+\})/g, function (a) {
            return args[+(a.substr(1, a.length - 2)) || 0];
        });
    };
}

const debugBox = document.getElementById("debugBox");
const uploadList = document.getElementById("uploadList");
const fileSelection = document.getElementById("fileSelection");
let fileList = [];

let uploadStatusTemplate;
let uploadStatusTemplateRequest = new XMLHttpRequest();
uploadStatusTemplateRequest.onload = function () {
    uploadStatusTemplate = uploadStatusTemplateRequest.responseText;
}
uploadStatusTemplateRequest.open("GET", "/assets/html/uploadStatusTemplate.html");
uploadStatusTemplateRequest.send();


function uploadFile(file) {
    let formData = new FormData();
    formData.set("file", file);

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    let request = new XMLHttpRequest();
    let url = window.location.origin + "/upload";

    request.onreadystatechange = function () {
        const fileName = file.name;
        if (request.readyState === XMLHttpRequest.OPENED) {
            let uploadEntry = uploadStatusTemplate.format(fileName, "response.url", "response.deletionUrl");

            let uploadEntryContainer = document.createElement("div");
            uploadEntryContainer.setAttribute("id", fileName)
            uploadEntryContainer.setAttribute("class", "mb-2");
            uploadEntryContainer.innerHTML = uploadEntry
            uploadList.appendChild(uploadEntryContainer);
        } else if (request.readyState === XMLHttpRequest.DONE) {
            let uploadEntryContainer = document.getElementById(fileName);
            let progressBar = uploadEntryContainer.getElementsByClassName("progress")[0]
                .getElementsByClassName("progress-bar")[0];
            if (request.status === 200) {
                let response = JSON.parse(request.responseText);

                uploadEntryContainer.getElementsByClassName("fileUrl")[0].innerHTML = response.url;
                uploadEntryContainer.getElementsByClassName("fileUrl")[0]
                    .setAttribute("href", `//${response.url}`);
                uploadEntryContainer.getElementsByClassName("fileDUrl")[0].innerHTML = response.deletionUrl;
                uploadEntryContainer.getElementsByClassName("fileDUrl")[0]
                    .setAttribute("href", `//${response.deletionUrl}`);

                uploadEntryContainer.getElementsByClassName("fileLinks")[0]
                    .setAttribute("style", "display: initial");

                progressBar.setAttribute("class", "progress-bar bg-success");
                progressBar.innerHTML = "Done!";
            } else {
                progressBar.setAttribute("class", "progress-bar bg-danger");
                progressBar.innerHTML = `Error! (${request.status})`;

                //alert(`error(${request.status}): ${request.responseText}`);
            }
        }
    }
    request.upload.addEventListener("progress", function (ev) {
        const fileName = file.name;
        let uploadEntryContainer = document.getElementById(fileName);
        let percent = (ev.loaded / ev.total) * 100;
        let progressBar = uploadEntryContainer.getElementsByClassName("progress")[0]
            .getElementsByClassName("progress-bar")[0];
        progressBar.innerHTML = `${percent}%`;
        progressBar.setAttribute("style", `width: ${percent}%`);
    });
    request.addEventListener("error", function (ev) {
        alert(`error: ${request.responseText}`);
    })

    request.open("POST", url);
    request.setRequestHeader(header, token);
    request.send(formData);
}

fileSelection.addEventListener("change", function (event) {
    fileList = [];
    for (let i = 0; i < fileSelection.files.length; i++) {
        fileList.push(fileSelection.files[i]);
    }
})

const fileForm = document.getElementById("fileForm")

fileForm.addEventListener("submit", function (event) {
    event.preventDefault();
    uploadList.replaceChildren();
    fileList.forEach(function (file) {
        uploadFile(file);
    })
})

debugBox.innerHTML += "<br>Attached<br>";