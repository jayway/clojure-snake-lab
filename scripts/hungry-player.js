const http = require("http");

async function getUrl(url) {
  return new Promise((resolve, reject) => {
    http.get(url, res => {
      let content = "";
      res.setEncoding("utf-8");
      res.on("data", chunk => (content += chunk));
      res.on("end", () => resolve(JSON.parse(content)));
    });
  });
}

async function wait(time) {
  return new Promise((resolve, reject) => {
    setTimeout(resolve, time);
  });
}

function makeMove({ width, height, snake, fruit }) {
  const [x, y] = snake[0];
  const [fx, fy] = fruit;

  if (fx < x) return "WEST";
  if (fx > x) return "EAST";
  if (fy < y) return "SOUTH";
  if (fy > y) return "NORTH";
  return "I DONT KNOW";
}

async function main() {
  let data = await getUrl("http://localhost:3000/start/whatnot");
  while (true) {
    await wait(100);
    if (data.alive) {
      const direction = makeMove(data);
      console.log("MOVING", direction);
      data = await getUrl(
        `http://localhost:3000/move/whatnot?direction=${direction}`
      );
      if (data.alive === false) console.log("died");
    } else {
      console.log("WTF!", data.alive);
      data = await getUrl("http://localhost:3000/start/whatnot");
    }
  }
}

main();
