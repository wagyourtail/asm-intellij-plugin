/*
 *
 *  Copyright 2011 Cédric Champeau
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.objectweb.asm.idea.plugin.view;

import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowManager;
import com.nasller.asm.libs.org.objectweb.asm.ClassReader;
import com.nasller.asm.libs.org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.idea.plugin.common.FileTypeExtension;
import org.objectweb.asm.idea.plugin.config.ASMPluginComponent;
import org.objectweb.asm.idea.plugin.config.ApplicationConfig;
import org.objectweb.asm.idea.plugin.util.GroovifiedTextifier;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The groovified view displays @groovyx.ast.bytecode.Bytecode code for methods.
 */
public class GroovifiedView extends ACodeView {

	public GroovifiedView(final Project project) {
		super(ToolWindowManager.getInstance(project), KeymapManager.getInstance(), project, FileTypeExtension.GROOVY.getValue());
	}

	public static GroovifiedView getInstance(Project project) {
		return project.getService(GroovifiedView.class);
	}

	@Override
	protected void loadFile(VirtualFile file) {
		StringWriter stringWriter = new StringWriter();
		ClassReader reader;
		try {
			file.refresh(false, false);
			reader = new ClassReader(file.contentsToByteArray());
		} catch (IOException e) {
			return;
		}
		int flags = 0;
		ApplicationConfig applicationConfig = ASMPluginComponent.getApplicationConfig();
		if (applicationConfig.isSkipDebug()) flags = flags | ClassReader.SKIP_DEBUG;
		if (applicationConfig.isSkipFrames()) flags = flags | ClassReader.SKIP_FRAMES;
		if (applicationConfig.isExpandFrames()) flags = flags | ClassReader.EXPAND_FRAMES;
		if (applicationConfig.isSkipCode()) flags = flags | ClassReader.SKIP_CODE;

		reader.accept(new TraceClassVisitor(null, new GroovifiedTextifier(applicationConfig.getGroovyCodeStyle()), new PrintWriter(stringWriter)), ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
		setCode(file, stringWriter.toString());
	}
}
