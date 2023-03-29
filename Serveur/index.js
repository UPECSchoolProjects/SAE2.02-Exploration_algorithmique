const path = require('path');
const result = require('dotenv').config({ path: path.resolve(__dirname, '.env') });
const express = require('express');
const http = require('http');
const bodyParser = require('body-parser');
const config = require('./config/config.json');
const httpsConfig = require('./config/httpsConfig.json');
const cors = require('cors');

console.log(result);

const port = process.env.PORT || config.port || 3000;

const srv = express();

srv.use(cors());
srv.use(bodyParser.json({ limit: '100mb', extended: true }))
srv.use(bodyParser.urlencoded({ limit: '100mb', extended: true }))
srv.use(express.static('files'))


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