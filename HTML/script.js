const HOST = "http://localhost:3000";
let chart = null;
let nbwords = 15;

async function fetchAsync(url) {
    // fonction pour récuperer un fichier sur le serveur
    let response = await fetch(url);
    let data = await response.text();
    console.log("Code :" + response.status)
    return data;
}

async function CSVToArray(strData, strDelimiter) {
    // fonction pour convertir un fichier csv en tableau
    strDelimiter = (strDelimiter || ";");
    let lines = strData.split("\n");
    let arr = [];
    for (let i = 1; i < lines.length; i++) {
        let obj = {};
        let currentline = lines[i].split(strDelimiter);
        obj["x"] = currentline[0];
        obj["value"] = currentline[1];
        obj["freq"] = currentline[2];
        arr.push(obj);
    }
    return arr;
}

function chartSize(data, nbwords) {
    data.sort(function (a, b) {
        return b.value - a.value;
    });

    //keep only the 15 most spoken languages
    data = data.slice(0, nbwords);

    return data
}

async function getCSVData(dataURL, nbwords) {
    console.debug(dataURL)
    let data = await CSVToArray(await fetchAsync(dataURL));
    return chartSize(data, nbwords);
}

async function drawChart(dataURL, nbwords) {
    console.log(dataURL)
    // fonction pour créer le nuage de mots
    // create a tag (word) cloud chart
    chart = anychart.tagCloud(await getCSVData(dataURL, nbwords));

    // set a chart title
    chart.title('Nuage de mots')
    // set an array of angles at which the words will be laid out
    chart.angles([0, 10, -10])
    // enable a color range
    //chart.colorRange(true);
    // set the color range length
    chart.tooltip().format(`Le mot "{%x}" apparait {%freq}% des fois ({%value} fois)`);

    // display the word cloud chart
    chart.container("container");
    chart.draw();
}

async function populateSelect() {
    // fonction pour remplir le selecteur de fichier
    let select = document.getElementById("selector");
    try {
        data = await fetchAsync(HOST + "/?all=true")
        let files = JSON.parse(data).filesObj;

        //groupBy folder
        let folders = new Map();
        for (file of files) {
            let folder = file.folder;
            if (!folders.has(folder)) {
                folders.set(folder, []);
            }
            folders.get(folder).push(file);
        }

        // create optionGroup and put files inside
        for (let [folder, files] of folders) {
            let optGroup = document.createElement("optgroup");
            optGroup.label = folder;

            for (file of files) {
                let opt = document.createElement("option");
                let filename = file.filename;
                if (!filename.endsWith(".csv")) continue;
                opt.value = filename;
                opt.innerHTML = filename.substring(0, filename.length - 4);
                optGroup.appendChild(opt);
            }

            if (optGroup.children.length > 0) {
                select.appendChild(optGroup);
            }
        }
    } catch (e) {
        console.error(e)
        let opt = document.createElement("option");
        opt.value = "error";
        opt.innerHTML = "Couldn't load files...";
        select.appendChild(opt);
    }
}

function getSelectedFile() {
    // fonction pour récuperer le fichier sélectionné
    let select = document.getElementById("selector");
    let selectedOption = select.selectedIndex == -1 ? 0 : select.selectedIndex;
    let file = select.options[selectedOption].value;
    console.log(file)

    let folder = select.options[selectedOption].parentNode.label
    if(!folder.endsWith("/")) folder += "/";

    return {filename: file, folder: folder};
}

async function changeFile() {
    // fonction pour changer le fichier affiché
    console.log("changed file to " + document.getElementById("selector").value + " !");
    let file = getSelectedFile();
    chart.data(await getCSVData(HOST + file.folder + file.filename, nbwords));
}

async function ready() {
    // fonction pour lancer le nuage de mots
    await populateSelect();
    let path = getSelectedFile();

    drawChart(HOST + path.folder + path.filename, 15);
}

document.addEventListener("DOMContentLoaded", ready);
document.getElementById("selector").addEventListener("change", changeFile);
document.getElementById("nbwords").addEventListener("change", ($event) => {
    let value = $event.target.value;
    console.log(value)
    if(isNaN(value) || value == "") return;
    console.log("changed nbwords to " + value + " !");
    nbwords = value;
    changeFile();
});