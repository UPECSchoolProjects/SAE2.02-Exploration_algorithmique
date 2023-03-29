console.log("Hello World")

async function fetchAsync(url) {
    // fonction pour récuperer un fichier sur le serveur
    let response = await fetch(url);
    let data = await response.text();
    console.log("Code :" + response.status)
    return data;
}

fetchAsync("http://localhost:3000/frequences.csv")

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

anychart.onDocumentReady(async function () {
    // fonction pour créer le nuage de mots
    var data = await CSVToArray(await fetchAsync("http://localhost:3000/frequences.csv"));
    data = chartSize(data, 500);
    // create a tag (word) cloud chart
    var chart = anychart.tagCloud(data);

    // set a chart title
    chart.title('Nuage de mots')
    // set an array of angles at which the words will be laid out
    chart.angles([0, 10, -10])
    // enable a color range
    chart.colorRange(true);
    // set the color range length
    chart.tooltip().format(`Le mot "{%x}" apparait {%freq}% des fois ({%value} fois)`);

    // display the word cloud chart
    chart.container("container");
    chart.draw();
});