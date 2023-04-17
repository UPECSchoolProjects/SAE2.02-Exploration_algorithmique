const HOST = "https://sae202.nwo.ovh/api";
// let selectorHTML = null;
// let chart = null;
// let container = null;
let nbwordHTML = null;
let nbwords = 15;
let datas = null;
let dataFiles = null;
let cosineSimHTML = null;
let lentxt1 = null;
let lentxt2 = null;

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
        obj["racine"] = currentline[1];
        obj["value"] = currentline[2];
        obj["freq"] = currentline[3];
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

async function drawChart(dataURL, data, filename) {
    console.log(dataURL)

    if (data.chart) {
        data.chart.dispose();
    }
    data.chartContainer.innerHTML = "";
    // fonction pour créer le nuage de mots
    // create a tag (word) cloud chart
    data.chart = anychart.tagCloud(await getCSVData(dataURL, nbwords));

    // set a chart title
    data.chart.title(filename)
    // set an array of angles at which the words will be laid out
    data.chart.angles([0, 10, -10])
    // enable a color range
    //chart.colorRange(true);
    // set the color range length
    data.chart.tooltip().format(`Le mot "{%x}" apparait {%freq}% des fois ({%value} fois), sa racine est {%racine}`);

    // display the word cloud chart
    data.chart.container(data.chartContainer);
    data.chart.autoRedraw(false);
    await data.chart.draw(true);
    await compareText();
}

async function populateSelect(selectorHTML) {
    // fonction pour remplir le selecteur de fichier
    try {
        if (dataFiles == null) {
            dataFetched = await fetchAsync(HOST + "/?all=true")
            dataFiles = JSON.parse(dataFetched).filesObj;
        }

        console.log("DDD : " + selectorHTML)
        console.log("111 : " + data)

        //groupBy folder
        let folders = new Map();
        for (file of dataFiles) {
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
                selectorHTML.appendChild(optGroup);
            }
        }
    } catch (e) {
        console.error(e)
        let opt = document.createElement("option");
        opt.value = "error";
        opt.innerHTML = "Couldn't load files...";
        selectorHTML.appendChild(opt);
    }

    console.log("EEE : " + selectorHTML)
}

function getSelectedFile(selectorHTML) {
    console.log('333 : ' + selectorHTML)
    // fonction pour récuperer le fichier sélectionné
    console.log("SelectorHTML :" + selectorHTML)
    let selectedOption = selectorHTML.selectedIndex == -1 ? 0 : selectorHTML.selectedIndex;
    let file = selectorHTML.options[selectedOption].value;
    console.log(file)

    let folder = selectorHTML.options[selectedOption].parentNode.label
    if (!folder.endsWith("/")) folder += "/";

    return { filename: file, folder: folder };
}

async function changeFile(nbData) {
    // fonction pour changer le fichier affiche
    let data = datas.get(nbData);
    let file = getSelectedFile(data.selectorHTML);

    updateURL(file.filename, file.folder, nbData);

    // disable fields
    nbwordHTML.disabled = true;
    data.selectorHTML.disabled = true;

    await drawChart(HOST + file.folder + file.filename, data, file.filename.substring(0, file.filename.length - 4));

    // enable fields
    nbwordHTML.disabled = false;
    data.selectorHTML.disabled = false;
}


function updateURL(filename, path, nbData) {
    // change params in url

    if (path.endsWith("/")) path = path.substring(0, path.length - 1);

    let url = new URL(window.location.href);
    url.searchParams.set("file" + nbData, filename);
    url.searchParams.set("path" + nbData, path);
    url.searchParams.set("nbwords", nbwords);
    console.log(url.toString());
    window.history.pushState(null, "", url.toString());
}

function updateNbWord(value) {
    console.log(value)
    if (isNaN(value) || value == "") return;
    console.log("changed nbwords to " + value + " !");
    nbwords = value;
    // index of all chart
    for (let [index, data] of datas) {
        changeFile(index);
    }

}

function searchInSelector(path, filename, selectorHTML) {
    let options = selectorHTML.options;
    for (let i = 0; i < options.length; i++) {
        if (options[i].parentNode.label == path && options[i].value == filename) {
            return i;
        }
    }
    return -1;
}

async function ready() {


    let aSeul = document.getElementById("seul");
    let multi = document.getElementById("multi");

    // event on a redirect with params
    aSeul.addEventListener("click", ($event) => {
        $event.preventDefault();
        // redirect with params
        let params = window.location.search;


        // fiile to redirect is seul.html
        window.location.href = "seul.html" + params;
    });

    multi.addEventListener("click", ($event) => {
        $event.preventDefault();
        // redirect with params
        let params = window.location.search;
        // fiile to redirect is comparaison.html
        window.location.href = "comparaison.html" + params;
    });



    datas = new Map()

    nbwordHTML = document.getElementById("nbwords");

    console.log("AAA : " + nbwordHTML)

    let urlParams = new URLSearchParams(window.location.search);
    let nbwordsURL = urlParams.get("nbwords");

    if (nbwordsURL) {
        if (!isNaN(nbwordsURL)) {
            nbwords = nbwordsURL;
            nbwordHTML.value = nbwordsURL;
        }
    }

    cosineSimHTML = document.getElementById("cosine");
    lentxt1 = document.getElementById("len1");
    lentxt2 = document.getElementById("len2");


    // get initial val
    nbwords = nbwordHTML.value;
    console.log(nbwords)

    nbwordHTML.addEventListener("change", ($event) => {
        let value = $event.target.value;
        updateNbWord(value);
    });

    await initContainer("chart0", "selector0", 0);
    if (document.getElementById("chart1") != null) {
        await initContainer("chart1", "selector1", 1);
    }

    await compareText();
}

async function initContainer(chartContainerId, selecterHTMLId, nb) {
    data = {
        chartContainer: document.getElementById(chartContainerId),
        selectorHTML: document.getElementById(selecterHTMLId),
        chart: null,
        currentString: null
    }

    console.log(data)

    // disable fields
    nbwordHTML.disabled = true;
    data.selectorHTML.disabled = true;

    // fonction pour lancer le nuage de mots

    console.log("CCCC :" + data.selectorHTML)

    await populateSelect(data.selectorHTML);

    console.log("BBBB :" + data.selectorHTML)

    // get url params
    let urlParams = new URLSearchParams(window.location.search);
    let pathURL = urlParams.get("path" + nb);
    let filenameURL = urlParams.get("file" + nb);

    console.log("pathURL" + nb + " :" + pathURL)
    console.log("filenameURL" + nb + " :" + filenameURL)

    if (pathURL && filenameURL) {
        let index = searchInSelector(pathURL, filenameURL, data.selectorHTML);
        if (index != -1) {
            data.selectorHTML.selectedIndex = index;
        }
    }


    console.log("AAAA :" + data.selectorHTML)
    let path = getSelectedFile(data.selectorHTML);


    await drawChart(HOST + path.folder + path.filename, data, path.filename.substring(0, path.filename.length - 4));

    data.selectorHTML.addEventListener("change", () => {
        changeFile(nb);
    });

    // enable fields
    nbwordHTML.disabled = false;
    data.selectorHTML.disabled = false;

    datas.set(nb, data);
}

async function compareText() {
    console.log('Compare text')
    console.log(datas.size)
    // if two datas
    if (datas.size != 2) return;

    // fetch text 1
    let data1 = datas.get(0);
    let file1 = getSelectedFile(data1.selectorHTML);
    let filename1 = file1.filename.substring(0, file1.filename.length - 4);

    // fetch text 2
    let data2 = datas.get(1);
    let file2 = getSelectedFile(data2.selectorHTML);
    let filename2 = file2.filename.substring(0, file2.filename.length - 4);


    let strTxt1 = await fetchAsync(HOST + file1.folder + filename1 + ".txt");
    let strTxt2 = await fetchAsync(HOST + file2.folder + filename2 + ".txt");

    //cosine_simularity_py = pyscript.interpreter.globals.get('cosine_simularity_py') 
    //let result = await cosine_simularity_py(strTxt1, strTxt2);
    cosine_simularity_py = pyscript.interpreter.globals.get('cosine_simularity_py') 
    let result = await cosine_simularity_py(strTxt1, strTxt2);

    let reslentxt1 = strTxt1.split(" ").length;
    let reslentxt2 = strTxt2.split(" ").length;

    cosineSimHTML.innerHTML = result;
    lentxt1.innerHTML = reslentxt1;
    lentxt2.innerHTML = reslentxt2;


    console.log(cosineSimHTML)
    console.log(lentxt1);
}

document.addEventListener("DOMContentLoaded", ready);

window.onpopstate = function(e) {
    window.location.reload();
};