/* --------------------------------------------------------------------------------------------
 * Copyright (c) 2018 TypeFox GmbH (http://www.typefox.io). All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for license information.
 * ------------------------------------------------------------------------------------------ */
import { listen, MessageConnection } from 'vscode-ws-jsonrpc';
import {
    MonacoLanguageClient, CloseAction, ErrorAction,
    MonacoServices, createConnection
} from 'monaco-languageclient';
const ReconnectingWebSocket = require('reconnecting-websocket');

const LANGUAGE_ID = 'java';
const MODEL_URI = 'file://no.host/model.java'
const MONACO_URI = monaco.Uri.parse(MODEL_URI);

// register Monaco languages
monaco.languages.register({
    id: LANGUAGE_ID,
    extensions: ['.java'],
    aliases: ['JAVA', 'Java'],
    mimetypes: ['text/plain'],
});

// create Monaco editor
const value = `package example;

import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * This code contains a misuse example CogniCrypt_SAST of a Cipher object. 
 * CogniCrypt_SAST reports that the string argument to Cipher.getInstance("AES/ECB/PKCS5Padding") does not correspond the CrySL specification. 
 *
 */
public class ConstraintErrorExample {
	public static void main(String...args) throws NoSuchAlgorithmException, NoSuchPaddingException {
		Cipher instance = Cipher.getInstance("AES/ECB/PKCS5Padding");  
	}
}

`

const editor = monaco.editor.create(document.getElementById("container")!, {
    model: monaco.editor.createModel(value, LANGUAGE_ID, MONACO_URI),
    glyphMargin: true,
    lightbulb: {
        enabled: true
    }
});

function createWebSocket(url: string): WebSocket {
    const socketOptions = {
        maxReconnectionDelay: 10000,
        minReconnectionDelay: 1000,
        reconnectionDelayGrowFactor: 1.3,
        connectionTimeout: 10000,
        maxRetries: Infinity,
        debug: false
    };
    return new ReconnectingWebSocket(url, undefined, socketOptions);
}

function createLanguageClient(connection: MessageConnection): MonacoLanguageClient {
    return new MonacoLanguageClient({
        name: "Sample Language Client",
        clientOptions: {
            // use a language id as a document selector
            documentSelector: [LANGUAGE_ID],
            // disable the default error handler
            errorHandler: {
                error: () => ErrorAction.Continue,
                closed: () => CloseAction.DoNotRestart
            }
        },
        // create a language client connection from the JSON RPC connection on demand
        connectionProvider: {
            get: (errorHandler, closeHandler) => {
                return Promise.resolve(createConnection(connection, errorHandler, closeHandler))
            }
        }
    });
}

// install Monaco language client services
MonacoServices.install(editor);

// create the web socket
const webSocket = createWebSocket('ws://localhost:8080/crypto-lsp-demo/websocket');

// listen when the web socket is opened
listen({
    webSocket,
    onConnection: connection => {
        // create and start the language client
        const languageClient = createLanguageClient(connection);
        const disposable = languageClient.start();
        connection.onClose(() => disposable.dispose());
    }
});
