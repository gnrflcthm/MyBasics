package com.mybasics.util;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.dankito.richtexteditor.android.command.BoldCommand;
import net.dankito.richtexteditor.android.command.DecreaseIndentCommand;
import net.dankito.richtexteditor.android.command.IncreaseIndentCommand;
import net.dankito.richtexteditor.android.command.InsertBulletListCommand;
import net.dankito.richtexteditor.android.command.InsertNumberedListCommand;
import net.dankito.richtexteditor.android.command.ItalicCommand;
import net.dankito.richtexteditor.android.command.RedoCommand;
import net.dankito.richtexteditor.android.command.UnderlineCommand;
import net.dankito.richtexteditor.android.command.UndoCommand;
import net.dankito.richtexteditor.android.toolbar.EditorToolbar;

/**
 * Custom implementation of NoteEditorToolbar.
 */
public class NoteEditorToolbar extends EditorToolbar {

    public NoteEditorToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initToolbars(context);
    }

    protected void initToolbars(Context context) {
        addCommand(new BoldCommand());
        addCommand(new ItalicCommand());
        addCommand(new UnderlineCommand());
        addCommand(new DecreaseIndentCommand());
        addCommand(new IncreaseIndentCommand());
        addCommand(new InsertBulletListCommand());
        addCommand(new InsertNumberedListCommand());
        addCommand(new UndoCommand());
        addCommand(new RedoCommand());
    }
}
