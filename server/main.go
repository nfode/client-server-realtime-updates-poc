package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"time"

	"io/ioutil"

	"github.com/centrifugal/gocent"
	"github.com/labstack/echo"
	"github.com/labstack/echo/middleware"
)

var todos []Todo

type Todo struct {
	ID      string `json:"id"`
	Text    string `json:"text"`
	Checked bool   `json:"checked"`
}
type TodoResponse struct {
	Data []Todo `json:"data"`
}

// blob
func LoadTodos() {
	b, err := ioutil.ReadFile("todos.json")
	if err != nil {
		fmt.Print(err)
	}
	err = json.Unmarshal(b, &todos)
	/*  */
}
func AllTodos(c echo.Context) error {
	response := TodoResponse{
		Data: todos,
	}
	jsonTodos := toJson(response)
	return c.String(http.StatusOK, jsonTodos)
}

func Update(c echo.Context) error {
	id := c.Param("id")
	var res = -1
	for i, _ := range todos {
		if todos[i].ID == id {
			res = i
		}
	}
	if res == -1 {
		return c.String(http.StatusNotFound, "")
	}
	todo := &todos[res]
	todo.Checked = !todo.Checked
	server := gocent.NewClient("http://localhost:8000", "secret", 5*time.Second)

	ch := "public:update"
	// How to publish.
	ok, err := server.Publish(ch, []byte(toJson(todo)))
	if err != nil {
		println(err.Error())
		return c.String(http.StatusBadRequest, "")
	}
	fmt.Printf("Publish into channel %s successful: %v\n", ch, ok)
	return c.String(http.StatusOK, toJson(todo))
}

func toJson(p interface{}) string {
	bytes, err := json.Marshal(p)
	if err != nil {
		fmt.Println(err.Error())
		os.Exit(1)
	}

	return string(bytes)
}

func main() {
	LoadTodos()
	e := echo.New()

	// Middleware
	e.Use(middleware.Logger())
	e.Use(middleware.Recover())
	e.GET("/todos/", AllTodos)
	e.POST("/update/:id", Update)
	e.Logger.Fatal(e.Start(":8080"))
}
