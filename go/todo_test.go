/*
 * Copyright (c) 2023 Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at
 * https://oss.oracle.com/licenses/upl.
 */

package main

import (
	"bufio"
	. "github.com/onsi/gomega"
	"io"
	"sync"
	"testing"
	"time"
)

// TestToDo tests the todo app.
func TestToDo(t *testing.T) {
	g := NewWithT(t)
	var wg sync.WaitGroup
	initialize()
	defer shutdown()

	// create a pipe to send commands to
	reader, writer := io.Pipe()
	scanner := bufio.NewScanner(reader)
	wg.Add(1)
	go func() {
		runREPL(scanner)
		wg.Done()
	}()

	_, err := writer.Write([]byte("help\n"))
	g.Expect(err).Should(Not(HaveOccurred()))
	sleep(5)

	_, err = writer.Write([]byte("add get food\n"))
	g.Expect(err).Should(Not(HaveOccurred()))
	sleep(5)

	_, err = writer.Write([]byte("list\n"))
	g.Expect(err).Should(Not(HaveOccurred()))
	sleep(5)

	_, err = writer.Write([]byte("complete 1\n"))
	g.Expect(err).Should(Not(HaveOccurred()))
	sleep(5)

	_, err = writer.Write([]byte("clear\n"))
	g.Expect(err).Should(Not(HaveOccurred()))
	sleep(5)

	_, err = writer.Write([]byte("quit\n"))
	g.Expect(err).Should(Not(HaveOccurred()))
	sleep(5)

	wg.Wait()
}

// Sleep will sleep for a duration of seconds
func sleep(seconds int) {
	time.Sleep(time.Duration(seconds) * time.Second)
}
