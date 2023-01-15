const searchQueryForm = document.getElementById("searchQueryForm");
const searchQuery = document.getElementById("searchQuery");
const searchButton = document.getElementById("searchButton");

searchQueryForm.addEventListener("submit", function (event) {
    event.preventDefault();
    searchButton.click()
});

searchButton.addEventListener("click", function () {
    let url = new URL(window.location);
    url.searchParams.set("searchQuery", searchQuery.value);
    window.location = url;
})

window.onload = function () {
    let url = new URL(window.location);
    if(url.searchParams.has("searchQuery")){
        searchQuery.value = url.searchParams.get("searchQuery");
    }
}