'use strict';

import * as path from 'path';
import * as os from 'os';

import {Trace} from 'vscode-jsonrpc';
import { commands, window, workspace, ExtensionContext, Uri } from 'vscode';
import { LanguageClient, LanguageClientOptions, ServerOptions } from 'vscode-languageclient';
import { lstat } from 'fs';

export function activate(context: ExtensionContext) {
    let script = 'java';
    let args = ['-jar',context.asAbsolutePath(path.join('..', 'target', 'crypto-lsp-demo-0.0.1-SNAPSHOT.jar')),"-c", context.asAbsolutePath('..')];
    let serverOptions: ServerOptions = {
        run : { command: script, args: args },
        debug: { command: script, args: args} //, options: { env: createDebugEnv() }
    };
    
    let clientOptions: LanguageClientOptions = {
        documentSelector: [{ scheme: 'file', language: 'java' }],
        synchronize: {
            configurationSection: 'java',
            fileEvents: [ workspace.createFileSystemWatcher('**/*.java') ]
        }
    };
    
    // Create the language client and start the client.
    let lc : LanguageClient = new LanguageClient('crypto-lsp-demo','Crypto LSP Demo Server', serverOptions, clientOptions);
    lc.start();
}

