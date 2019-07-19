export namespace Utils {

  export class StringUtil {
    public static endsWith( source, target ) {
      let start = source.length - target.length;
      return start >= 0 && source.indexOf(target, start) == start;
    }

    public static isEmpty( value ) {
      return (!value || value == undefined || value == "" || value.length == 0);
    }

    /**
     * textarea 생성 후 클립보드에 복사
     * @param text
     */
    public static copyToClipboard(text: string) {
      let textarea = null;
      textarea = document.createElement("textarea");
      textarea.style.height = "0px";
      textarea.style.left = "-100px";
      textarea.style.opacity = "0";
      textarea.style.position = "fixed";
      textarea.style.top = "-100px";
      textarea.style.width = "0px";
      document.body.appendChild(textarea);
      // Set and select the value (creating an active Selection range).
      textarea.value = text;
      textarea.select();
      // Ask the browser to copy the current selection to the clipboard.
      let successful = document.execCommand("copy");
      if (successful) {
        // do something
      } else {
        // handle the error
      }
      if (textarea && textarea.parentNode) {
        textarea.parentNode.removeChild(textarea);
      }
    }
  }

  export class Generate {
    /**
     * UUID 생성
     */
    public static UUID() {
      let d = new Date().getTime();
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
        let r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c == 'x' ? r : (r & 0x3 | 0x8)).toString(16);
      });
    }
  }

}
