const http = require('http');

async function getUrl(url) {
    return new Promise((resolve, reject) => {
        http.get(url, res => {
            let content = '';
            res.setEncoding('utf-8');
            res.on('data', chunk => content += chunk);
            res.on('end', () => resolve(JSON.parse(content)));
        })
    })
}

async function wait(time) {
    return new Promise((resolve, reject) => { setTimeout(resolve, time)});
}

function makeMove({ width, height, snake, fruit}) {
    const [x, y] = snake[0];
    console.log(x, y);
    if (x > 0 && y === 0) return 'WEST';
    if (x === 0 && y < height - 1) return 'NORTH';
    if (y === height - 1 && x < width - 1) return 'EAST';
    return 'SOUTH';
}

async function main() {
    let data = await getUrl('http://localhost:3000/start');
    while (true) {
        await wait(100);
        if (data.alive) {
            const direction = makeMove(data);
            console.log('MOVING', direction);
            data = await getUrl(`http://localhost:3000/move?direction=${direction}`);
        } else {
            data = await getUrl('http://localhost:3000/start');
        }
    }
}

main();