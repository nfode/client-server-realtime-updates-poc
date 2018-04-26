package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"time"

	"io/ioutil"

	"github.com/labstack/echo"
	"github.com/labstack/echo/middleware"
)

var todos []Todo
var centrifugo *Centrifugo

// LoadTodos reads all todos from a file
func LoadTodos() {
	b, err := ioutil.ReadFile("../todos.json")
	if err != nil {
		fmt.Print(err)
	}
	err = json.Unmarshal(b, &todos)
}

// GetAllTodos return all available todos on a get request
func GetAllTodos(c echo.Context) error {
	response := TodoResponse{
		Data: todos,
	}
	jsonTodos := toJSON(response)
	return c.String(http.StatusOK, jsonTodos)
}

// UpdateTask toogles the checkeck attribute of a todo on a post call
func UpdateTask(c echo.Context) error {
	id := c.Param("id")
	var res = -1
	for i := range todos {
		if todos[i].ID == id {
			res = i
		}
	}
	if res == -1 {
		return c.String(http.StatusNotFound, "")
	}
	todo := &todos[res]
	todo.Checked = !todo.Checked
	fmt.Println(todos)
	ch := "public:update"
	// How to publish.
	ok, err := centrifugo.Publish(ch, []byte(toJSON(todo)))
	if err != nil {
		fmt.Print(err)
		return c.String(http.StatusBadRequest, "")
	}
	fmt.Printf("Publish into channel %s successful: %v\n", ch, ok)
	return c.String(http.StatusOK, toJSON(todo))
}

func toJSON(p interface{}) string {
	bytes, err := json.Marshal(p)
	if err != nil {
		fmt.Println(err.Error())
		os.Exit(1)
	}
	return string(bytes)
}

func main() {
	LoadTodos()
	addr := os.Getenv("CENTRIFUGO_HOST")
	port := os.Getenv("CENTRIFUGO_PORT")
	addr = addr + ":" + port
	centrifugo = NewClient("http://"+addr, "secret", 5*time.Second)
	e := echo.New()
	// Middleware
	e.Use(middleware.Logger())
	e.Use(middleware.Recover())
	e.GET("/todos/", GetAllTodos)
	e.POST("/update/:id", UpdateTask)
	e.Logger.Fatal(e.Start(":8080"))
}
