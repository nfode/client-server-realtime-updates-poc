package main

import (
	"time"

	"github.com/centrifugal/gocent"
)

// Centrifugo represents the connection to a centrifugo server
type Centrifugo struct {
	Client *gocent.Client
}

// NewClient create a new Centrifugo client
func NewClient(addr, secret string, timeout time.Duration) *Centrifugo {
	client := gocent.NewClient(addr, secret, timeout)
	return &Centrifugo{
		Client: client,
	}
}

// Publish publishes a payload to a channel
func (c *Centrifugo) Publish(channel string, payload []byte) (bool, error) {
	ok, err := c.Client.Publish(channel, payload)
	return ok, err
}
