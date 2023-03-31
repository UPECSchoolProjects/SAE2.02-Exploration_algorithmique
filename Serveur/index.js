const path = require('path');
const result = require('dotenv').config({ path: path.resolve(__dirname, '.env') });
const express = require('express');
const http = require('http');
const bodyParser = require('body-parser');
const config = require('./config/config.json');
const httpsConfig = require('./config/httpsConfig.json');
const cors = require('cors');
const fs = require("fs");

console.log(result);

const port = process.env.PORT || config.port || 3000;
const directory = process.env.DIRECTORY || config.directory || 'files';
const directoryPath = path.resolve(__dirname, directory);

const srv = express();

srv.use(cors());
srv.use(bodyParser.json({ limit: '100mb', extended: true }))
srv.use(bodyParser.urlencoded({ limit: '100mb', extended: true }))
srv.use(express.static(directoryPath))

srv.get('/', async (req, res) => {
    let folder = req.query.folder || "";
    let allOption = req.query.all || false;
    console.log(allOption)
    // list all files in the files folder
    try {
        let files = await fs.readdirSync(path.resolve(directoryPath, folder));
        let filesObj = [];
        for (let i = 0; i < files.length; i++) {
            const stats = await fs.statSync(path.resolve(directoryPath, folder, files[i]));

            filesObj.push({ filename: files[i], isDir: stats.isDirectory(), folder: "/" })
        }
        if (allOption) {
            console.log("allOption if")
            for (file of filesObj) {
                console.log("file :", file)
                if (file.isDir) {
                    console.log(file.filename)
                    let files = await fs.readdirSync(path.resolve(directoryPath, folder, file.filename));
                    for (fileChild of files) {
                        const stats = await fs.statSync(path.resolve(directoryPath, folder, file.filename, fileChild));
                        filesObj.push({ filename: fileChild, isDir: stats.isDirectory(), folder: "/" + file.filename })
                    }
                }
            }
        }
        res.status(200).json({ filesObj });
    } catch (err) {
        console.log(err);
        res.status(500).json({ error: err });
    }
});


if (httpsConfig.useNGINX) {
    srv.listen(port, console.log(`Server running on port ${port} using NGINX`));
} else {
    if (httpsConfig.certpath !== "" && httpsConfig.keypath !== "") {
        try {
            https
                .createServer(
                    {
                        key: fs.readFileSync(httpsConfig.keypath),
                        cert: fs.readFileSync(httpsConfig.certpath)
                    },
                    srv
                )
                .listen(port, () => {
                    console.log("Serveur started using protocol HTTPS on port " + port);
                });
        } catch (err) {
            console.log('Serveur failed, try restarting it with administrator privilege');
        }
    } else {
        if (httpsConfig.allowHTTP) {
            http.createServer(srv).listen(port, () => {
                console.log("Serveur started using protocol HTTP on port " + port);
            });
        } else {
            console.log('Couldn\'t start using HTTPS, allowHTTP is false, server stopped.');
        }
    }
}