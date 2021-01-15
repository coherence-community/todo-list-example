define(['exports', 'ojs/ojvcomponent'], function (exports, ojvcomponent) { 'use strict';

    /**
     * @license
     * Copyright (c) 2014, 2020, Oracle and/or its affiliates.
     * The Universal Permissive License (UPL), Version 1.0
     * as shown at https://oss.oracle.com/licenses/upl/
     * @ignore
     */
    /**
     * @ojcomponent oj.ojAvatar
     * @ojtsvcomponent
     * @ojsignature {target: "Type", value: "class ojAvatar extends JetElement<ojAvatarSettableProperties>"}
     * @since 4.0.0
     *
     * @ojshortdesc An avatar represents a person or entity as initials or an image.
     *
     * @ojpropertylayout {propertyGroup: "common", items: ["size", "background"]}
     * @ojpropertylayout {propertyGroup: "data", items: ["src", "initials"]}
     * @ojvbdefaultcolumns 2
     * @ojvbmincolumns 1
     *
     * @ojuxspecs ['avatar']
     *
     * @classdesc
     * <h3 id="avatarOverview-section">
     *   JET Avatar
     *   <a class="bookmarkable-link" title="Bookmarkable Link" href="#avatarOverview-section"></a>
     * </h3>
     * <p>Description: Themeable, WAI-ARIA-compliant element that often represents a person.</p>
     * <p>A JET avatar is an icon capable of displaying
     * a custom image, or initials, or a placeholder image.  The order of precedence for
     * what is displayed, in order from highest to lowest, is:</p>
     * <ol>
     *  <li>Custom image specified through the "src" attribute</li>
     *  <li>Initials specified through the "initials" attribute</li>
     *  <li>Default placeholder image</li>
     * </ol>
     * <pre class="prettyprint">
     * <code>//Avatar with initials
     *&lt;oj-avatar initials="AB">
     * &lt;/oj-avatar>
     *</code></pre>
     *  <h3 id="a11y-section">
     *   Accessibility
     *   <a class="bookmarkable-link" title="Bookmarkable Link" href="#a11y-section"></a>
     *  </h3>
     *
     * <p>To make the component accessible, the application must set the role attribute of oj-avatar to 'img' and provide a value for
     * the aria-label attribute.  JET Avatar does not have
     * any interaction with the application, therefore it is not keyboard navigable by default.
     * The aria-label will be picked up by the tabbable/focusable parent unless it is
     * overriden by the application.
     * The application can set a tooltip by setting the title attribute of the avatar.  It is recommended that the title and aria-label attributes are in sync.
     *
     *
     * <p>In order to meet accessibility requirements for text, color contrast ratio
     * between the background color and text must be
     * greater than 4.5 for the two smallest avatars and 3.1 for the five larger avatars.
     * Avatar's default background satisfies the 3.1 color contrast ratio.  The background
     * will automatically be darkened for the two smallest sizes to satisfy the more
     * stringent 4.5 contrast ratio.  If colors are customized through theming, the
     * application is responsible for specifying colors that satisfy the 3.1 contrast ratio.
     * The custom background color will be automatically darkened as well for the two smallest avatars.
     *
     * <p>The src attribute will display the image as a background-image.  These images do
     * not appear in high contrast mode.  For this reason, initials must be specified and
     * and will be shown instead.
     *
     *
     * <h3 id="image-section">
     *   Image
     *   <a class="bookmarkable-link" title="Bookmarkable Link" href="#image-section"></a>
     *  </h3>
     *
     * <p>The avatar will display the image provided from the src attribute if the src
     * attribute is populated.  If the src attribute is not provided and the initials have been,
     * the initials will be displayed.  If neither src nor initials attributes are populated,
     * a single person placeholder image is shown.
     * Use the class oj-avatar-group-image to use the group placeholder image.
     * Examples displaying each type of avatar:
     * <pre class="prettyprint">
     * <code>//Individual Placeholder
     * &lt;oj-avatar>
     *  &lt;/oj-avatar>
     * //Group Placeholder
     * &lt;oj-avatar class="oj-avatar-group-image">
     *  &lt;/oj-avatar>
     * //Initials
     *&lt;oj-avatar initials="AB">
     * &lt;/oj-avatar>
     * //Image
     *&lt;oj-avatar initials="AB" src="image.jpg">
     * &lt;/oj-avatar>
     *</code></pre>
     *
     */
    /**
     * Specifies the size of the avatar.
     * @expose
     * @name size
     * @memberof oj.ojAvatar
     * @ojshortdesc Specifies the size of the avatar.
     * @instance
     * @type {string}
     * @ojvalue {string} "xxs" {"description": "extra, extra small avatar", "displayName": "Extra Extra Small"}
     * @ojvalue {string} "xs" {"description": "extra small avatar", "displayName": "Extra Small"}
     * @ojvalue {string} "sm" {"description": "small avatar", "displayName": "Small"}
     * @ojvalue {string} "md" {"description": "medium avatar (default, if unspecified)", "displayName": "Medium"}
     * @ojvalue {string} "lg" {"description": "large avatar", "displayName": "Large"}
     * @ojvalue {string} "xl" {"description": "extra large avatar", "displayName": "Extra Large"}
     * @ojvalue {string} "xxl" {"description": "extra, extra large avatar", "displayName": "Extra Extra Large"}
     * @ojvalueskeeporder
     * @default "md"
     * @example <caption>Renders avatar displaying default placeholder image with <code class="prettyprint">size</code>
     * attribute set to large</caption>
     * &lt;oj-avatar size='lg'>&lt;/oj-avatar>
     * @example <caption>Get or set the <code class="prettyprint">size</code> property after initialization</caption>
     * //Get avatar size
     * var size = myAvatar.size;
     *
     * //Set size property to xs
     * myAvatar.size = "xs";
     */
    /**
     * Specifies the initials of the avatar.  Will only be displayed if the src attribute is null.
     * Required if src attribute is provided for accessibility purposes.  Will be displayed
     * if the src attribute is not specified, or in high contrast mode for accessibility
     * purposes.
     * @expose
     * @name initials
     * @ojtranslatable
     * @ojshortdesc Specifies the initials of the avatar.
     * @memberof oj.ojAvatar
     * @instance
     * @type {string|null}
     * @default null
     * @example <caption>Renders a default medium avatar with initials</caption>
     * &lt;oj-avatar initials='AB'>&lt;/oj-avatar>
     * @example <caption>Get or set the <code class="prettyprint">initials</code> property after initialization</caption>
     * //Get avatar initials
     * var initials = myAvatar.initials;
     *
     * //Set initials property to 'NT'
     * myAvatar.initials = "NT";
     */
    /**
     * Specifies the src for the image of the avatar.  Image will be rendered as a background image.
     * In high contrast mode, initials will be displayed instead since background images
     * will not be rendered.
     * @ojshortdesc Specifies the source for the image of the avatar.
     * @expose
     * @name src
     * @memberof oj.ojAvatar
     * @instance
     * @type {string|null}
     * @default null
     * @example <caption>Renders a default medium avatar with a image</caption>
     * &lt;oj-avatar src='image.jpg'>&lt;/oj-avatar>
     * @example <caption>Get or set the <code class="prettyprint">src</code> property after initialization</caption>
     * //Get avatar src
     * var src = myAvatar.src;
     *
     * //Set src property to 'image2.jpg'
     * myAvatar.src = "image2.jpg";
     */
    /**
     * Specifies the background of the avatar.
     * @expose
     * @name background
     * @memberof oj.ojAvatar
     * @ojshortdesc Specifies the background of the avatar.
     * @instance
     * @type {string}
     * @ojvalue {string} "neutral" {"description": "Neutral background (default, if unspecified)", "displayName": "Neutral"}
     * @ojvalue {string} "red" {"description": "Red background", "displayName": "Red"}
     * @ojvalue {string} "orange" {"description": "Orange background", "displayName": "Orange"}
     * @ojvalue {string} "forest" {"description": "Forest background", "displayName": "Forest"}
     * @ojvalue {string} "green" {"description": "Green background", "displayName": "Green"}
     * @ojvalue {string} "teal" {"description": "Teal background", "displayName": "Teal"}
     * @ojvalue {string} "mauve" {"description": "Mauve background", "displayName": "Mauve"}
     * @ojvalue {string} "purple" {"description": "Purple background", "displayName": "Purple"}
     * @ojvalueskeeporder
     * @default "neutral"
     */

    // --------------------------------------------------- Styling ------------------------------------------------------------
    /**
     * @classdesc The following CSS classes can be applied by the page author as needed.<br/>
     */
    // ---------------- oj-avatar-group-image --------------
    /**
     * Use to display avatar group placeholder image instead of single person placeholder image.
     * @ojstyleclass oj-avatar-group-image
     * @ojdisplayname Avatar Group Image
     * @memberof oj.ojAvatar
     * @ojtsexample
     * //Group Placeholder
     * &lt;oj-avatar class="oj-avatar-group-image">
     * &lt;/oj-avatar>
     */

    /**
     * Sets a property or a single subproperty for complex properties and notifies the component
     * of the change, triggering a [property]Changed event.
     *
     * @function setProperty
     * @param {string} property - The property name to set. Supports dot notation for subproperty access.
     * @param {any} value - The new value to set the property to.
     *
     * @expose
     * @memberof oj.ojAvatar
     * @ojshortdesc Sets a property or a single subproperty for complex properties and notifies the component of the change, triggering a corresponding event.
     * @instance
     * @return {void}
     *
     * @example <caption>Set a single subproperty of a complex property:</caption>
     * myComponent.setProperty('complexProperty.subProperty1.subProperty2', "someValue");
     */
    /**
     * Retrieves a value for a property or a single subproperty for complex properties.
     * @function getProperty
     * @param {string} property - The property name to get. Supports dot notation for subproperty access.
     * @return {any}
     *
     * @expose
     * @memberof oj.ojAvatar
     * @instance
     *
     * @example <caption>Get a single subproperty of a complex property:</caption>
     * var subpropValue = myComponent.getProperty('complexProperty.subProperty1.subProperty2');
     */
    /**
     * Performs a batch set of properties.
     * @function setProperties
     * @param {Object} properties - An object containing the property and value pairs to set.
     * @return {void}
     *
     * @expose
     * @memberof oj.ojAvatar
     * @instance
     *
     * @example <caption>Set a batch of properties:</caption>
     * myComponent.setProperties({"prop1": "value1", "prop2.subprop": "value2", "prop3": "value3"});
     */

    var __decorate = (null && null.__decorate) || function (decorators, target, key, desc) {
        var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
        if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
        else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
        return c > 3 && r && Object.defineProperty(target, key, r), r;
    };
    class Props {
        constructor() {
            this.background = 'neutral';
            this.initials = null;
            this.size = 'md';
            this.src = null;
        }
    }
    exports.Avatar = class Avatar extends ojvcomponent.VComponent {
        render() {
            const props = this.props;
            const classNameObj = {
                'oj-avatar': true,
                'oj-avatar-has-initials': !!(props.initials && !props.src),
                'oj-avatar-image': !!props.src,
                ['oj-avatar-bg-' + props.background]: true,
                ['oj-avatar-' + props.size]: true
            };
            let innerContent;
            if (props.src) {
                innerContent = (ojvcomponent.h("div", { class: 'oj-avatar-background-image', style: { backgroundImage: `url("${props.src}")` } }));
            }
            else if (props.initials) {
                innerContent = (ojvcomponent.h("div", { class: 'oj-avatar-initials oj-avatar-background-image' }, props.initials));
            }
            else {
                innerContent = (ojvcomponent.h("div", { class: 'oj-avatar-background-image' },
                    ojvcomponent.h("div", { class: 'oj-avatar-placeholder' })));
            }
            return (ojvcomponent.h("div", { class: classNameObj, "aria-hidden": 'true' }, innerContent));
        }
    };
    exports.Avatar.metadata = { "extension": { "_DEFAULTS": Props }, "properties": { "background": { "type": "string", "enumValues": ["neutral", "red", "orange", "forest", "green", "teal", "mauve", "purple"], "value": "neutral" }, "initials": { "type": "string|null", "value": null }, "size": { "type": "string", "enumValues": ["xxs", "xs", "sm", "md", "lg", "xl", "xxl"], "value": "md" }, "src": { "type": "string|null", "value": null } } };
    exports.Avatar = __decorate([
        ojvcomponent.customElement('oj-avatar')
    ], exports.Avatar);

    Object.defineProperty(exports, '__esModule', { value: true });

});
