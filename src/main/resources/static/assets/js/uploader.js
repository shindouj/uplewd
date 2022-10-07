if (!String.prototype.format) {
    String.prototype.format = function(...args) {
        return this.replace(/(\{\d+\})/g, function(a) {
            return args[+(a.substr(1, a.length - 2)) || 0];
        });
    };
}

const debugBox = document.getElementById("debugBox");
const uploadList = document.getElementById("uploadList");
const fileSelection = document.getElementById("fileSelection");
let fileList = [];

const uploadStatusTemplate = `
    <div class="card bg-light p-0 h-100 shadow-sm">
        <div class="row h-100 g-0 rounded flex-nowrap">
            <div class="col-auto bg-info rounded-start">&nbsp;</div>
            <div class="col p-3 py-4 text-info">
                <div class="d-flex flex-row">
                    <h6 class="text-truncate text-uppercase">URL: </h6>
                    <h6 class="text-truncate text-uppercase">
                        <a href="//{0}">{0}</a>
                    </h6>
                </div>
                <div class="d-flex flex-row">
                    <h6 class="text-truncate text-uppercase">Deletion URL: </h6>
                    <h6 class="text-truncate text-uppercase">
                        <a href="//{1}">{1}</a>
                    </h6>
                </div>
            </div>
            <div class="col-auto d-flex rounded-end">
                <h1 class="mb-0 mx-auto align-self-center">
                    <a href="#" title="View details"
                        class="nav-link text-info pb-0">
                        <span class="d-inline-block lnr lnr-cart"></span>
                        </a>
                </h1>
            </div>
        </div>
    </div>
`;

function uploadFile(file) {
    let formData = new FormData();
    formData.set("file", file);

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    let request = new XMLHttpRequest();
    let url = window.location.origin + "/upload";
    request.onreadystatechange = function () {
        if(request.readyState === XMLHttpRequest.DONE){
            let response = JSON.parse(request.responseText);

            //debugBox.innerHTML += request.responseText;
            //debugBox.innerHTML += "<br>";

            let uploadEntry = uploadStatusTemplate.format(response.url, response.deletionUrl);
            uploadList.innerHTML += uploadEntry
        }
    }

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
    fileList.forEach(function (file) {
        uploadFile(file);
    })
})

debugBox.innerHTML += "<br>Attached<br>";