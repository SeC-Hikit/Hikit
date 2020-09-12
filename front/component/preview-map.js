let previewMap = Vue.component("preview-map", {
  template: `
        <div class="map-preview" id="map-table"></div>
  `,
  props: {
    points: Array,
  },
  data: {
    startPoint: [0, 0],
    endPoint: [0, 0],
  },
  methods: {
    onPreview: function () {
      alert("Opening " + this.points);
    },
  },
  watch: {
    points: function () {
      this.onPreview();
    },
  },
  mounted: function () {
    var map = L.map("map-table").setView([44.498955, 11.327591], 9);

    //  Topographic:
    // https://{s}.tile.opentopomap.org/{z}/{x}/{y}.png

    L.tileLayer("https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png", {
      attribution:
        '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
    }).addTo(map);

    L.marker([44.498955, 11.327591])
      .addTo(map)
      .bindPopup("A pretty CSS3 popup.<br> Easily customizable.")
      .openPopup();
  },
});

module.exports = previewMap;
