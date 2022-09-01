function uploadFile(file) {
    let formData = new FormData();
    formData.set("file", file);

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    let request = new XMLHttpRequest();
    let url = window.location.origin + "/upload";
    request.open("POST", url);
    request.setRequestHeader(header, token);
    request.send(formData);
}

const fileSelection = document.getElementById("fileSelection");
let fileList = [];

fileSelection.addEventListener("change", function (event) {
    fileList = [];
    for (let i = 0; i < fileSelection.files.length; i++) {
        fileList.push(fileSelection.files[i]);
    }
})

const fileForm = document.getElementById("fileForm")

fileForm.addEventListener("submit", function (event) {
    event.preventDefault();
    fileList.forEach(function (file) {
        uploadFile(file);
    })
})
