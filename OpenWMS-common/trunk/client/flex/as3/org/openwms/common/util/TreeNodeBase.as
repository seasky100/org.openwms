/**
 * Generated by Gas3 v2.0.0 (Granite Data Services).
 *
 * WARNING: DO NOT CHANGE THIS FILE. IT MAY BE OVERWRITTEN EACH TIME YOU USE
 * THE GENERATOR. INSTEAD, EDIT THE INHERITED INTERFACE (TreeNode.as).
 */

package org.openwms.common.util {

    import java.util.Iterator;

    public interface TreeNodeBase {

        function get children():Iterator;

        function set data(value:Object):void;
        function get data():Object;

        function get leaf():Boolean;

        function set parent(value:TreeNode):void;
        function get parent():TreeNode;
    }
}