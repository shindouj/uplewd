if (!String.prototype.format) {
    String.prototype.format = function (...args) {
        return this.replace(/{([0-9]+)}/g, function (match, index) {
            return typeof args[index] == 'undefined' ? match : args[index];
        });
    };
}

const uploadList = document.getElementById("uploadList");
const fileSelection = document.getElementById("fileSelection");
const fileForm = document.getElementById("fileForm");

let fileList = [];

let uploadStatusTemplate;
let uploadStatusTemplateRequest = new XMLHttpRequest();
uploadStatusTemplateRequest.onload = function () {
    uploadStatusTemplate = uploadStatusTemplateRequest.responseText;
}
uploadStatusTemplateRequest.open("GET", "/assets/html/uploadStatusTemplate.html");
uploadStatusTemplateRequest.send();

fileSelection.addEventListener("change", function () {
    fileList = [];
    for (let i = 0; i < fileSelection.files.length; i++) {
        fileList.push(fileSelection.files[i]);
    }
});

fileForm.addEventListener("submit", function (event) {
    event.preventDefault();
    uploadList.replaceChildren();
    fileList.forEach(function (file) {
        uploadFile(file);
    })
});

function uploadFile(file) {
    let formData = new FormData();
    formData.set("file", file);

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    let url = window.location.origin + "/upload";

    let request = new XMLHttpRequest();
    request.onreadystatechange = function () {
        if (request.readyState === XMLHttpRequest.OPENED) {
            let uploadEntry = uploadStatusTemplate.format(file.name, "response.url", "response.deletionUrl");
            let uploadEntryContainer = document.createElement("div");
            uploadEntryContainer.setAttribute("id", file.name);
            uploadEntryContainer.setAttribute("class", "mb-2");
            uploadEntryContainer.innerHTML = uploadEntry;

            uploadList.appendChild(uploadEntryContainer);
        } else if (request.readyState === XMLHttpRequest.DONE) {
            let uploadEntryContainer = document.getElementById(file.name);
            let progressBar = uploadEntryContainer.getElementsByClassName("progress")[0]
                .getElementsByClassName("progress-bar")[0];
            if (request.status === 200) {
                let response = JSON.parse(request.responseText);

                let fileUrl = uploadEntryContainer.getElementsByClassName("fileUrl")[0];
                fileUrl.innerHTML = response.url;
                fileUrl.setAttribute("href", `//${response.url}`);

                let fileDUrl = uploadEntryContainer.getElementsByClassName("fileDUrl")[0];
                fileDUrl.innerHTML = response.deletionUrl;
                fileDUrl.setAttribute("href", `//${response.deletionUrl}`);

                uploadEntryContainer.getElementsByClassName("fileLinks")[0]
                    .classList.remove("d-none");

                progressBar.setAttribute("class", "progress-bar bg-success");
                progressBar.innerHTML = "Done!";
            } else {
                progressBar.setAttribute("class", "progress-bar bg-danger");
                progressBar.innerHTML = `Error! (${request.status})`;
            }
        }
    };

    request.upload.addEventListener("progress", function (ev) {
        let uploadEntryContainer = document.getElementById(file.name);
        let percent = (ev.loaded / ev.total) * 100;
        let progressBar = uploadEntryContainer.getElementsByClassName("progress")[0]
            .getElementsByClassName("progress-bar")[0];
        progressBar.innerHTML = `${percent}%`;
        progressBar.setAttribute("style", `width: ${percent}%`);
    });

    request.addEventListener("error", function () {
        alert(`error: ${request.responseText}`);
    });

    request.open("POST", url);
    request.setRequestHeader(header, token);
    request.send(formData);
}